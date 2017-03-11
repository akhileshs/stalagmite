package testing.optimiseheap

// @data(
//   optimiseHeapOptions = true,
//   optimiseHeapBooleans = true,
//   optimiseHeapStrings = true
// )
// class Foo(a: Option[Boolean], b: Option[Boolean], s: Option[String])
final class Foo private (
  private[this] val _bitmask: Long,
  private[this] val _s: Array[Char]
) extends Serializable {

  def a: Option[Boolean] = {
    if ((_bitmask & 0x00) != 0) None
    else Some((_bitmask & 0x01) != 0) // optimiseHeapBooleans
  }
  def b: Option[Boolean] = {
    if ((_bitmask & 0x02) != 0) None
    else Some((_bitmask & 0x03) != 0) // optimiseHeapBooleans
  }
  def s: Option[String] = {
    if (_s == null) None
    else Some(new String(_s)) // optimiseHeapStrings
  }

  def copy(
    a: Option[Boolean] = a,
    b: Option[Boolean] = b,
    s: Option[String] = s
  ): Foo = ???

  override def toString(): String = ???
  override def hashCode: Int = ???
  override def equals(o: Any): Boolean = ??? // NOTE can't use eq for _s when optimiseHeapString is used

  // should use the public field accesors, not the internal ones
  private[this] def writeObject(out: java.io.ObjectOutputStream): Unit = ???
  private[this] def readObject(in: java.io.ObjectInputStream): Unit = ???

  private[this] def readResolve(raw: Foo) = Foo(raw.a, raw.b, raw.s)

}

final object Foo extends ((Option[Boolean], Option[Boolean], Option[String]) => Foo) {
  private[this] def readResolve(raw: Foo.type): Foo.type = Foo

  def apply(a: Option[Boolean], b: Option[Boolean], s: Option[String]): Foo = ???
  def unapply(a: Option[Boolean], b: Option[Boolean], s: Option[String]): Option[Foo] = ???

  // no type parameters, so this can be a val
  implicit val LabelledGenericFoo: shapeless.LabelledGeneric[Foo] = null
}
