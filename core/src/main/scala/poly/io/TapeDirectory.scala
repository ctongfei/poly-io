package poly.io

/**
 * Represents a directory in a tape file system.
 *
 * Its children are not directly obtainable from itself.
 * @author Tongfei Chen
 * @since 0.4.0
 */
trait TapeDirectory[S <: TapeFileSystem] extends TapePath[S] { self: S#Directory =>
  final def isDirectory: Boolean = true
  final def isFile: Boolean = false
  final def isSymLink: Boolean = false
}
