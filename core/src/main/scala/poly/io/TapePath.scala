package poly.io

/**
 * Represents a path in a tape file system.
 * @author Tongfei Chen
 * @since 0.4.0
 */
trait TapePath[S <: TapeFileSystem] { self: S#Path =>

  /** Returns a reference to the file system in which this file resides. */
  val fileSystem: S

  /** Returns an array of the components of this path.
   * @example {{{
   *   Directory("/home/admin/projects") = ("home", "admin", "projects")
   * }}} */
  def path: Array[String]

  // NAME MANIPULATION

  /** Returns the full name (including the absolute path) of this file. */
  def fullName = fileSystem.prefix + path.mkString(fileSystem.separator)

  /** Returns the name (without the path section) of this file or directory. */
  def name = path.last

  /**
   * Returns the extension of this file. The extension is lowercased.
   * @return If there's no extension, the empty string `""` is returned.
   */
  def extension = {
    val dotPos = name.lastIndexOf('.')
    if (dotPos == -1) "" else name.substring(dotPos + 1).toLowerCase
  }

  /**
   * Returns the basename of a file name (without the extension part).
   */
  def baseName = {
    val dotPos = name.lastIndexOf('.')
    if (dotPos == -1) name else name.substring(0, dotPos)
  }

  // PERMISSIONS
  def permissions: scala.collection.Set[PosixFilePermission]
  def isHidden: Boolean
  def isReadable: Boolean
  def isWriteable: Boolean
  def isExecutable: Boolean

  def isDirectory: Boolean
  def isFile: Boolean
  def isSymLink: Boolean

  // OVERRIDING JVM MEMBERS

  override def hashCode = fullName.hashCode

  override def equals(that: Any) = that match {
    case that: fileSystem.Path if that.fileSystem eq this.fileSystem //
    => this.fullName == that.fullName
    case _ => false
  }

  override def toString = fullName

}
