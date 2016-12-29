package poly.io

/**
 * Represents a read-only file system.
 *
 * A file system can either be your local file system,
 * a zip archive, or a file system on a remote server.
 * @author Tongfei Chen
 * @since 0.3.0
 */
trait ReadOnlyFileSystem extends TapeFileSystem { self =>

  def prefix: String

  def separator: String

  type Path <: ReadOnlyPath[this.type]

  type Directory <: Path with ReadOnlyDirectory[this.type]

  type File <: Path with ReadOnlyFile[this.type]

  type SymLink <: Path with ReadOnlySymLink[this.type]

  /** Returns the root directory of this file system. */
  def root: Directory

  def createPath(xs: Array[String]): Path

  def createDirectory(xs: Array[String]): Directory

  def createFile(xs: Array[String]): File

  def createSymLink(xs: Array[String]): SymLink

  def paths = root.recursiveChildren

  def files = root.recursiveFiles

}
