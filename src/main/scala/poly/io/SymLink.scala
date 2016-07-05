package poly.io

/**
 * @author Tongfei Chen
 */
trait SymLink[S <: FileSystem[S]] { self: S#SymLink =>

  /** Returns the target of this symbolic link. */
  def target: fileSystem.Path

}
