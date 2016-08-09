package poly.io

/**
 * Represents a directory under a specific file system.
 * @author Tongfei Chen
 * @since 0.2.0
 */
trait Directory[S <: FileSystem[S]] extends Path[S] { self: S#Directory =>

  /** Returns an iterable sequence of all the immediate children of this directory. */
  def children: Iterable[fileSystem.Path]

  /** Returns an iterable sequence of all children of this directory, recursively. */
  def recursiveChildren: Iterable[fileSystem.Path]

  def subdirectories: Iterable[fileSystem.Directory]

  def files: Iterable[fileSystem.File]

  def recursiveSubdirectories: Iterable[fileSystem.Directory]

  def recursiveFiles: Iterable[fileSystem.File]

  def /(s: String): fileSystem.Directory

  def /!(s: String): fileSystem.File

  def /@(s: String): fileSystem.SymLink

  def clear(): Unit = for (c <- children) c.delete()

  def contains(name: String): Boolean

  def createDirectory(name: String): fileSystem.Directory

  def createFile(name: String): fileSystem.File

  def createSymLink(name: String, target: fileSystem.Path): fileSystem.SymLink

}
