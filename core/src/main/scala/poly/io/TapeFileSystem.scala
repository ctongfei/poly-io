package poly.io

/**
 * Represents a linear tape file system.
 *
 * There is not notion of directory tree in a tape file system:
 * files can only be iterated sequentially (an example would be tarballs).
 * @author Tongfei Chen
 * @since 0.4.0
 */
trait TapeFileSystem { self =>
  
  def prefix: String
  
  def separator: String

  type Path <: TapePath[this.type]

  type File <: TapeFile[this.type] with Path

  type Directory <: TapeDirectory[this.type] with Path

  type SymLink <: TapeSymLink[this.type] with Path

  def paths: Iterable[Path]

  def files: Iterable[File]

  def directories: Iterable[Directory]

}
