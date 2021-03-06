lazy val stalagmite = project
  .settings(
    inThisBuild(
      Seq(
        organization := "com.fommil",
        sonatypeGithub := ("fommil", "stalagmite"),
        licenses := Seq(LGPL3),
        scalaVersion := "2.12.3"
      )
    ),
    libraryDependencies ++= Seq(
      "com.google.guava"           % "guava" % "22.0" % "test",
      "com.google.guava"           % "guava-testlib" % "18.0" % "test",
      "org.ensime"                 %% "pcplod" % "1.2.1" % "test",
      "org.typelevel"              %% "cats" % "0.9.0" % "test",
      "org.typelevel"              %% "kittens" % "1.0.0-M10" % "test",
      "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.6" % "test",
      "com.github.fommil"          %% "spray-json-shapeless" % "1.4.0" % "test",
      "org.scalameta"              %% "testkit" % "1.8.0" % "test",
      "org.scalameta"              %% "scalameta" % "1.8.0" % Provided
    ) ++ shapeless.value.map(_     % "test"),
    addCompilerPlugin(
      "org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full
    ),
    scalacOptions += "-Xplugin-require:macroparadise",
    javaOptions in Test ++= Seq(
      s"""-Dpcplod.settings=${(scalacOptions in Test).value.mkString(",")}""",
      s"""-Dpcplod.classpath=${(fullClasspath in Test).value
        .map(_.data)
        .mkString(",")}"""
    ),
    scalacOptions in Test ++= Seq(
      "-Yno-imports",
      "-Yno-predef"
    ),
    scalafmtOnCompile in ThisBuild := true,
    scalafmtConfig in ThisBuild := file("project/scalafmt.conf"),
    scalafmtVersion in ThisBuild := "1.1.0",
    addCommandAlias("fmt", ";sbt:scalafmt ;scalafmt ;test:scalafmt"),
    // WORKAROUND https://github.com/scalameta/paradise/issues/10
    scalacOptions in (Compile, console) ~= (_ filterNot (_ contains "paradise")),
    // WORKAROUND https://github.com/scalameta/paradise/issues/216
    publishArtifact in (Compile, packageDoc) := false,
    wartremoverWarnings in (Compile, compile) := Seq(
      Wart.AsInstanceOf,
      Wart.EitherProjectionPartial,
      Wart.IsInstanceOf,
      Wart.TraversableOps,
      Wart.NonUnitStatements,
      Wart.Null,
      Wart.OptionPartial,
      Wart.Return,
      Wart.StringPlusAny,
      Wart.Throw,
      Wart.TryPartial,
      Wart.Var,
      Wart.FinalCaseClass,
      Wart.ExplicitImplicitTypes
    ),
    wartremoverWarnings in (Test, compile) := Seq(
      Wart.EitherProjectionPartial,
      Wart.TraversableOps,
      Wart.Return,
      Wart.StringPlusAny,
      Wart.TryPartial,
      Wart.FinalCaseClass,
      Wart.ExplicitImplicitTypes
    )
  )

lazy val bench =
  project
    .dependsOn(stalagmite % "test->test")
    .enablePlugins(JmhPlugin)
    .settings(
      sourceDirectory in Jmh := (sourceDirectory in Test).value,
      classDirectory in Jmh := (classDirectory in Test).value,
      dependencyClasspath in Jmh := (dependencyClasspath in Test).value,
      // rewire tasks, so that 'jmh:run' automatically invokes 'jmh:compile' (otherwise a clean 'jmh:run' would fail)
      compile in Jmh := (compile in Jmh).dependsOn(compile in Test).value,
      run in Jmh := (run in Jmh).dependsOn(compile in Jmh).evaluated
    )
