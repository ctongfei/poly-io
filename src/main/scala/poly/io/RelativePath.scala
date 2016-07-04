package poly.io

/**
 * Represents a relative path.
 * @author Tongfei Chen
 * @since 0.2.0
 */
class RelativePath(val path: Array[String]) {

  /** Returns the full name of this path. */
  def fullName = path.mkString("/")

  override def toString = fullName

  override def hashCode = fullName.hashCode

  override def equals(that: Any) = that match {
    case that: RelativePath => this.path sameElements that.path
  }

}

class RelativeDirectory(path: Array[String]) extends RelativePath(path)
class RelativeFile(path: Array[String]) extends RelativePath(path)
class RelativeSymLink(path: Array[String]) extends RelativePath(path)
