package poly.io

/**
 * Represents a directory under a specific file system.
 * @author Tongfei Chen
 * @since 0.2.0
 */
trait Directory[S <: FileSystem[S]] extends Path[S] { self: S#Directory =>

  def children: Iterable[S#Path]

  def subdirectories: Iterable[S#Directory]

  def files: Iterable[S#File]

  def recursiveSubdirectories: Iterable[S#Directory]

  def recursiveFiles: Iterable[S#File]

  def /(s: String): S#Directory

  def /!(s: String): S#File

  def /@(s: String): S#SymLink

  def exists(name: String): Boolean

  def createDirectory(name: String): S#Directory

  def createFile(name: String): S#File

  def createSymLink(name: String, target: S#Path): S#SymLink


}
