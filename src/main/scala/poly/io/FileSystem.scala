package poly.io

import poly.algebra._

/**
 * Represents a writable file system.
 *
 * A file system can either be your local file system,
 * a zip archive, or a file system on a remote server.
 * @since 0.2.0
 * @author Tongfei Chen
 */
trait FileSystem extends ReadOnlyFileSystem { self =>

  type Path <: poly.io.Path[this.type]

  type Directory <: poly.io.Directory[this.type] with Path

  type File <: poly.io.File[this.type] with Path

  type SymLink <: poly.io.ReadOnlySymLink[this.type] with Path

  implicit def transferProvider: Copying[this.type, this.type]


}
