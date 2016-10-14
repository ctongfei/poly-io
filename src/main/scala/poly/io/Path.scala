package poly.io

/**
 * Represents an abstract path under a specific file system.
 *
 * @tparam S Type of file system.
 * @author Tongfei Chen
 * @since 0.2.0
 */
trait Path[S <: FileSystem] extends ReadOnlyPath[S] { self: S#Path =>

  def rename(newName: String): Unit

  // COPYING & MOVING

  def moveTo[DS <: FileSystem](destination: DS#Directory)(implicit ft: FileTransferProvider[S, DS]) = ft.moveTo(self, destination)

  def copyTo[DS <: FileSystem](destination: DS#Directory)(implicit ft: FileTransferProvider[S, DS]) = ft.copyTo(self, destination)

  /** Removes this file or directory. */
  def delete(): Unit


}
