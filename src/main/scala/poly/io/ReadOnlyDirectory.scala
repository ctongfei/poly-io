package poly.io

import poly.io.util._

/**
 * Represents a directory in a read-only file system.
 *
 * @author Tongfei Chen
 * @since 0.3.0
 */
trait ReadOnlyDirectory[S <: ReadOnlyFileSystem] extends ReadOnlyPath[S] { self: S#Directory =>

  /** Returns an iterable sequence of all the immediate children of this directory. */
  def children: Iterable[fileSystem.Path]

  /** Returns an iterable sequence of all children of this directory, recursively. */
  def recursiveChildren: Iterable[fileSystem.Path] = new Iterable[fileSystem.Path] {
    def iterator = new DepthFirstTreeSearcher[fileSystem.Path](self.asInstanceOf[fileSystem.Path])(p => p match {
        case d: poly.io.ReadOnlyDirectory[fileSystem.type] if d.isDirectory => d.children
        case _ => Traversable.empty
      }
    )
  }

  def subdirectories: Iterable[fileSystem.Directory]

  def files: Iterable[fileSystem.File]

  def recursiveSubdirectories: Iterable[fileSystem.Directory] = new Iterable[fileSystem.Directory] {
    def iterator = new DepthFirstTreeSearcher[fileSystem.Directory](self.asInstanceOf[fileSystem.Directory])(_.subdirectories)
  }

  def recursiveFiles: Iterable[fileSystem.File] = new Iterable[fileSystem.File] {
    def iterator: Iterator[fileSystem.File] = recursiveChildren.iterator.filter(_.isFile).collect { case f: fileSystem.File => f } // type checked is erased!
  }

  def /(s: String): fileSystem.Directory

  def /!(s: String): fileSystem.File

  def /@(s: String): fileSystem.SymLink

  def contains(name: String): Boolean


  final def isDirectory: Boolean = true
  final def isFile: Boolean = false
  final def isSymLink: Boolean = false

}
