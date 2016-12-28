package poly.io

/**
 * @author Tongfei Chen
 */
trait ReadOnlySymLink[S <: ReadOnlyFileSystem] { self: S#SymLink =>

  /** Returns the target of this symbolic link. */
  def target: fileSystem.Path


  final def isDirectory: Boolean = false
  final def isFile: Boolean = false
  final def isSymLink: Boolean = true


}
