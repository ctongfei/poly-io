package poly.io

/**
 * Represents a writable file system.
 *
 * A file system can either be your local file system,
 * a zip archive, or a file system on a remote server.
 * @since 0.2.0
 * @author Tongfei Chen
 */
trait WritableFileSystem extends ReadOnlyFileSystem { self =>

  type Path <: WritablePath[this.type]

  type Directory <: WritableDirectory[this.type] with Path

  type File <: WritableFile[this.type] with Path

  type SymLink <: ReadOnlySymLink[this.type] with Path

  implicit def copying: Copying[this.type, this.type]


}
