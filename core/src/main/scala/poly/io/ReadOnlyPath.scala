package poly.io

/**
 * Represents an abstract path under a specific file system.
 *
 * @tparam S Type of file system.
 * @author Tongfei Chen
 * @since 0.3.0
 */
trait ReadOnlyPath[S <: ReadOnlyFileSystem] { self: S#Path =>

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

  // PATH MANIPULATION

  /** Returns the parent directory of this path. */
  def parent: fileSystem.Directory = fileSystem.createDirectory(path.init)

  def relativize(that: fileSystem.Directory) = new RelativeDirectory(Util.relativize(self.path, that.path))
  def relativize(that: fileSystem.File) = new RelativeFile(Util.relativize(self.path, that.path))
  def relativize(that: fileSystem.Path) = new RelativePath(Util.relativize(self.path, that.path))

  def resolve(rd: RelativeDirectory): fileSystem.Directory = fileSystem.createDirectory(Util.resolve(self.path, rd.path))
  def resolve(rf: RelativeFile): fileSystem.File = fileSystem.createFile(Util.resolve(self.path, rf.path))
  def resolve(rl: RelativeSymLink): fileSystem.SymLink = fileSystem.createSymLink(Util.resolve(self.path, rl.path))

  def /(rd: RelativeDirectory): fileSystem.Directory = resolve(rd)
  def /(rf: RelativeFile): fileSystem.File = resolve(rf)
  def /(rl: RelativeSymLink): fileSystem.SymLink = resolve(rl)

  // PERMISSIONS
  def permissions: scala.collection.Set[PosixFilePermission]
  def isHidden: Boolean
  def isReadable: Boolean
  def isWriteable: Boolean
  def isExecutable: Boolean

  def isDirectory: Boolean
  def isFile: Boolean
  def isSymLink: Boolean


  // COPYING
  def copyTo[DS <: FileSystem](destination: DS#Directory)(implicit ft: Copying[S, DS]) = ft.copyTo(self, destination)

  // OVERRIDING JVM MEMBERS

  override def hashCode = fullName.hashCode

  override def equals(that: Any) = that match {
    case that: fileSystem.Path if that.fileSystem eq this.fileSystem //
      => this.fullName == that.fullName
    case _ => false
  }

  override def toString = fullName

}
