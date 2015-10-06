package poly.io

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
case class RelativePath(path: Array[String]) {
  override def toString = path.mkString(FileSystem.separator)
}
