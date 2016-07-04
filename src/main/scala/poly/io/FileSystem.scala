package poly.io

/**
 * Represents a path system.
 * A path system specifies its prefix and its separator.
 *
 * @since 0.2.0
 * @author Tongfei Chen
 */
trait FileSystem[S <: FileSystem[S]] { self: S =>

  def prefix: String

  def separator: String

  type Path <: poly.io.Path[S]

  type Directory <: poly.io.Directory[S] with Path

  type File <: poly.io.File[S] with Path

  type SymLink <: poly.io.SymLink[S] with Path

  def root: Directory

  def directory(xs: Array[String]): Directory

  def file(xs: Array[String]): File

  def symLink(xs: Array[String]): SymLink

  def relativize(self: Path, that: Directory) = new RelativeDirectory(util.relativize(self.path, that.path))

  def resolve(self: Path, rd: RelativeDirectory): Directory = directory(util.resolve(self.path, rd.path))

}
