package poly.io


/**
 *
 * @tparam S Type of file system.
 * @author Tongfei Chen
 * @since 0.2.0
 */
trait Path[S <: FileSystem[S]] { self: S#Path =>

  val fileSystem: S

  def path: Array[String]

  // NAME MANIPULATION

  /** Returns the full name (including the absolute path) of this file. */
  def fullName = fileSystem.prefix + path.mkString(fileSystem.separator)

  /** Returns the name (without the path section) of this file or directory. */
  def name = path.last

  /**
   * Returns the extension of this file. The extension is lowercased.
   * @return If there's no extension, return the empty string `""`.
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

  // PATH MANIPULATION

  /** Returns the parent directory of this path. */
  def parent: S#Directory = fileSystem.directory(path.init)

  def relativize(that: S#Directory) = new RelativeDirectory(util.relativize(self.path, that.path))
  def relativize(that: S#File) = new RelativeFile(util.relativize(self.path, that.path))
  def relativize(that: S#Path) = new RelativePath(util.relativize(self.path, that.path))

  def resolve(rd: RelativeDirectory): S#Directory = fileSystem.directory(util.resolve(self.path, rd.path))
  def resolve(rf: RelativeFile): S#File = fileSystem.file(util.resolve(self.path, rf.path))

  // PERMISSIONS
  def permissions: scala.collection.mutable.Set[PosixFilePermission]
  def isReadable: Boolean
  def isWriteable: Boolean
  def isExecutable: Boolean

  // COPYING & MOVING

  def moveTo[DS <: FileSystem[DS]](destination: DS#Directory)(implicit ft: FileTransferring[S, DS]) = ft.moveTo(self, destination)

  def copyTo[DS <: FileSystem[DS]](destination: DS#Directory)(implicit ft: FileTransferring[S, DS]) = ft.copyTo(self, destination)

  /** Removes this file. */
  def delete(): Unit

  // OVERRIDING JVM MEMBERS

  override def hashCode = fullName.hashCode

  override def equals(that: Any) = that match {
    case that: S#Path => (this.fileSystem eq that.fileSystem) && this.fullName == that.fullName
    case _ => false
  }

  override def toString = fullName

}





