package poly.io

trait FileTransferring[S1 <: FileSystem[S1], S2 <: FileSystem[S2]] {

  def copyTo(f: S1#Path, d: S2#Directory): Unit
  def moveTo(f: S1#Path, d: S2#Directory): Unit

}
