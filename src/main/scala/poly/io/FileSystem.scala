package poly.io

import poly.algebra._

/**
 * Represents a file system.
 *
 * A file system can either be your local file system,
 * a zip archive, or a file system on a remote server.
 * @since 0.2.0
 * @author Tongfei Chen
 */
trait FileSystem { self =>

  def prefix: String

  def separator: String

  type Path <: poly.io.Path[this.type]

  type Directory <: poly.io.Directory[this.type] with Path

  type File <: poly.io.File[this.type] with Path

  type SymLink <: poly.io.SymLink[this.type] with Path

  def root: Directory

  def directory(xs: Array[String]): Directory

  def file(xs: Array[String]): File

  def symLink(xs: Array[String]): SymLink

  implicit def transferProvider: FileTransferProvider[this.type, this.type]

  /** The path semilattice / partial order of this file system. */
  implicit object PathStructure extends UpperSemilatticeWithEq[Path] with HasTop[Path] { //TODO: BoundedUpperSemilatticeWithEq?
    def top = root
    def sup(x: Path, y: Path) = {
      val lx = x.path.length
      val ly = x.path.length
      val l = util.lcpLength(x.path, y.path)
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
