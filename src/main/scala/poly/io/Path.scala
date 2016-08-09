package poly.io

/**
 * Represents an abstract path under a specific file system.
 *
 * @tparam S Type of file system.
 * @author Tongfei Chen
 * @since 0.2.0
 */ //TODO: poly.collection.node.BiOrderedTreeNode
trait Path[S <: FileSystem[S]] { self: S#Path =>

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

  def rename(newName: String): Unit

  // PATH MANIPULATION

  /** Returns the parent directory of this path. */
  def parent: fileSystem.Directory = fileSystem.directory(path.init)

  def relativize(that: fileSystem.Directory) = new RelativeDirectory(util.relativize(self.path, that.path))
  def relativize(that: fileSystem.File) = new RelativeFile(util.relativize(self.path, that.path))
  def relativize(that: fileSystem.Path) = new RelativePath(util.relativize(self.path, that.path))

  def resolve(rd: RelativeDirectory): fileSystem.Directory = fileSystem.directory(util.resolve(self.path, rd.path))
  def resolve(rf: RelativeFile): fileSystem.File = fileSystem.file(util.resolve(self.path, rf.path))
  def resolve(rl: RelativeSymLink): fileSystem.SymLink = fileSystem.symLink(util.resolve(self.path, rl.path))

  /** Returns the lowest common ancestor of two paths in the same file system.
   * @example {{{
   *    Directory("/home/a") lca Directory("/home/b") == Directory("/home")
   * }}}
   */
  def lca(that: fileSystem.Path) =
    fileSystem.PathStructure.sup(self.asInstanceOf[fileSystem.Path], that)
    // the typecast is actually safe: self is actually an instance of fileSystem.Path

  def /(rd: RelativeDirectory): fileSystem.Directory = resolve(rd)
  def /(rf: RelativeFile): fileSystem.File = resolve(rf)
  def /(rl: RelativeSymLink): fileSystem.SymLink = resolve(rl)

  // PERMISSIONS
  def permissions: scala.collection.mutable.Set[PosixFilePermission]
  def isHidden: Boolean
  def isReadable: Boolean
  def isWriteable: Boolean
  def isExecutable: Boolean

  // COPYING & MOVING

  def moveTo[DS <: FileSystem[DS]](destination: DS#Directory)(implicit ft: FileTransferProvider[S, DS]) = ft.moveTo(self, destination)

  def copyTo[DS <: FileSystem[DS]](destination: DS#Directory)(implicit ft: FileTransferProvider[S, DS]) = ft.copyTo(self, destination)

  /** Removes this file or directory. */
  def delete(): Unit

  // OVERRIDING JVM MEMBERS

  override def hashCode = fullName.hashCode

  override def equals(that: Any) = that match {
    case that: fileSystem.Path => (this.fileSystem eq that.fileSystem) && this.fullName == that.fullName
    case _ => false
  }

  override def toString = fullName

}


