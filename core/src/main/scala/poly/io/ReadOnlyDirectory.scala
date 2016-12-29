package poly.io

import poly.io.Util._
import scala.collection.{Map, DefaultMap}

/**
 * Represents a directory in a read-only file system.
 *
 * @author Tongfei Chen
 * @since 0.3.0
 */
trait ReadOnlyDirectory[S <: ReadOnlyFileSystem] extends ReadOnlyPath[S] with TapeDirectory[S] { self: S#Directory =>

  /** Returns an iterable sequence of all the immediate children of this directory. */
  def children: Iterable[fileSystem.Path]

  /** Returns an map that maps file names to their path object. */
  def childrenMap: Map[String, fileSystem.Path] = new DefaultMap[String, fileSystem.Path] {
    def get(k: String) = if (self.contains(k)) Some(self.fileSystem.createPath(self.path :+ k)) else None
    def iterator = children.iterator.map(p => p.name -> p)
  }

  /** Returns an iterable sequence of all children of this directory, recursively. */
  def recursiveChildren: Iterable[fileSystem.Path] = new Iterable[fileSystem.Path] {
    def iterator = new DepthFirstTreeSearcher[fileSystem.Path](self.asInstanceOf[fileSystem.Path])(p => p match {
        case d: poly.io.ReadOnlyDirectory[fileSystem.type] if d.isDirectory => d.children // type checking is erased!
        case _ => Traversable.empty
      }
    )
  }

  /** Returns a lazy stream of subdirectories in this directory (not including subdirectories of subdirectories). */
  def subdirectories: Iterable[fileSystem.Directory]

  /** Returns the files in this directory. */
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
}
