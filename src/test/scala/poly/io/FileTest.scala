package poly.io

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
object FileTest extends App {

  val r = Directory("/Users/tongfei/my/proj/sbt/poly-io/test")

  r.createFileIfNotExist("a.txt")
  val a = r /! "a.txt"
  println(a.extension)

  val a1 = a.chars.toArray
  val a2 = a.bytes.toArray
  val a3 = a.lines.toArray

  val obs = a.lineWriter
  obs.write("abc")
  obs.close()


  //f.permissions += PosixFilePermission.OthersWrite

  val bp = 0

}
