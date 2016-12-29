package poly.io

/**
 * Represents a directory under a specific file system.
 * @author Tongfei Chen
 * @since 0.2.0
 */
trait WritableDirectory[S <: WritableFileSystem] extends ReadOnlyDirectory[S] with WritablePath[S] { self: S#Directory =>

  def clear(): Unit = for (c <- children) c.delete()

  def createDirectory(name: String): fileSystem.Directory

  def createFile(name: String): fileSystem.File

  def createSymLink(name: String, target: fileSystem.Path): fileSystem.SymLink

}
