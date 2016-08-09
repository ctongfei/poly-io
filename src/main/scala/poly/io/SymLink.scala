package poly.io

/**
 * @author Tongfei Chen
 */
trait SymLink[S <: FileSystem] { self: S#SymLink =>

  /** Returns the target of this symbolic link. */
  def target: fileSystem.Path

}
