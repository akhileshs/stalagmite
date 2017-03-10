scalacOptions ++= Seq("-unchecked", "-deprecation")
ivyLoggingLevel := UpdateLogging.Quiet

addSbtPlugin("com.fommil" % "sbt-sensible" % "1.1.8")
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "1.5.1")
