package poly.io

/**
 * @author Tongfei Chen
 */
trait ReadOnlySymLink[S <: ReadOnlyFileSystem] extends ReadOnlyPath[S] with TapeSymLink[S] { self: S#SymLink =>

  /** Returns the target of this symbolic link. */
  def target: fileSystem.Path

}
