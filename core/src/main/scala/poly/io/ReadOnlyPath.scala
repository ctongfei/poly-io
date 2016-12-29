package poly.io

/**
 * Represents an abstract path under a specific file system.
 *
 * @tparam S Type of file system.
 * @author Tongfei Chen
 * @since 0.3.0
 */
trait ReadOnlyPath[S <: ReadOnlyFileSystem] extends TapePath[S] { self: S#Path =>

  // COPYING
  def copyTo[DS <: WritableFileSystem](destination: DS#Directory)(implicit ft: Copying[S, DS]) = ft.copyTo(self, destination)


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

}
