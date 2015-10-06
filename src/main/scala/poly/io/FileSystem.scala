package poly.io

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
object FileSystem {

  val prefix = if (System.getProperty("os.name").startsWith("Windows")) "" else "/"

  val separator = if (System.getProperty("os.name").startsWith("Windows")) "\\" else "/"

}
