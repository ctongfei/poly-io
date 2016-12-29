package poly.io

/**
 * @author Tongfei Chen
 */
trait TapeSymLink[S <: TapeFileSystem] { self: S#SymLink =>

  final def isDirectory: Boolean = false
  final def isFile: Boolean = false
  final def isSymLink: Boolean = true


}
