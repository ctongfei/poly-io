package poly.io

trait Copying[S1 <: ReadOnlyFileSystem, S2 <: FileSystem] {
  def copyTo(f: S1#Path, d: S2#Directory): Unit
  def moveTo(f: S1#Path, d: S2#Directory): Unit

}
