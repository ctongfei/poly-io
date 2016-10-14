package poly.io

import poly.algebra._

/**
 * Represents a read-only file system.
 *
 * A file system can either be your local file system,
 * a zip archive, or a file system on a remote server.
 * @author Tongfei Chen
 * @since 0.3.0
 */
trait ReadOnlyFileSystem { self =>

  def prefix: String

  def separator: String

  type Path <: poly.io.ReadOnlyPath[this.type]

  type Directory <: Path with poly.io.ReadOnlyDirectory[this.type]

  type File <: Path with poly.io.ReadOnlyFile[this.type]

  type SymLink <: Path with poly.io.ReadOnlySymLink[this.type]

  /** Returns the root directory of this file system. */
  def root: Directory

  def directory(xs: Array[String]): Directory

  def file(xs: Array[String]): File

  def symLink(xs: Array[String]): SymLink

  /** The path semilattice / partial order of this file system. */
  implicit object PathStructure extends UpperSemilatticeWithEq[Path] with HasTop[Path] { //TODO: BoundedUpperSemilatticeWithEq?
  def top = root
    def sup(x: Path, y: Path) = {
      val lx = x.path.length
      val ly = x.path.length
      val l = Util.lcpLength(x.path, y.path)

      if (l == lx) x
      else if (l == ly) y
      else directory(x.path.take(l))
    }
    override def eq(x: Path, y: Path) = x == y
    def le(x: Path, y: Path) = y.path startsWith x.path
  }

  implicit object PathHashing extends Hashing[Path] {
    def hash(x: Path) = x.##
    def eq(x: Path, y: Path) = x == y
  }
}
