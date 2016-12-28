package poly.io.archive

import poly.io._
import java.util.zip._
import scala.collection.mutable
import scala.collection.JavaConversions._

/**
 * Represents the file system inside a Zip archive.
 * @author Tongfei Chen
 * @since 0.3.0
 */
class ZipArchive private[io](zf: Local.File, enc: Codec) extends ReadOnlyFileSystem { zip =>

  def prefix = s"$zf!${zf.fileSystem.separator}"
  def separator = zf.fileSystem.separator

  val root = new Directory(Array())

  private[this] val jzf = new ZipFile(zf.toString, enc.charset)

  private[this] def createDir0(path: Array[String]): Directory = {
    var dir: Directory = root
    for (p <- path) {
      if (!(dir.ch contains p))
        dir.ch += p -> new Directory(dir.path :+ p)
      dir = dir.ch(p).asInstanceOf[Directory]
    }
    dir
  }

  private[this] def createFile0(path: Array[String], ze: ZipEntry): Unit = {
    val dir = createDir0(path.init)
    dir.ch += path.last -> new File(path, ze)
  }

  // Constructor: construct the tree structure inside this zip archive
  for (ze <- jzf.stream.iterator) {
    val name = ze.getName
    if (name endsWith "/") createDir0(name.split('/').filter(_ != ""))
    else createFile0(name.split('/'), ze)
  }

  sealed abstract class Path extends poly.io.ReadOnlyPath[zip.type] {
    val fileSystem: zip.type = zip
    def permissions = throw new UnsupportedOperationException("Zip archives does not support POSIX permissions")
    def isHidden = permissions
    def isReadable = true
    def isWriteable = false
    def isExecutable = permissions
  }

  class Directory private[io](val path: Array[String]) extends Path with poly.io.ReadOnlyDirectory[zip.type] {
    private[io] val ch = mutable.HashMap[String, Path]()
    def children: Iterable[Path] = ch.values
    def subdirectories: Iterable[Directory] = ch.values.collect { case d: Directory => d }
    def files: Iterable[File] = ch.values.collect { case f: File => f }
    def /(s: String): Directory = ch(s).asInstanceOf[Directory]
    def /!(s: String): File = ch(s).asInstanceOf[File]
    def /@(s: String): SymLink = throw new UnsupportedOperationException("Zip files do not support symbolic links.")
    def contains(name: String): Boolean = ch contains name
  }

  class File private[io](val path: Array[String], val ze: ZipEntry) extends Path with poly.io.ReadOnlyFile[zip.type] {
    def size = ze.getSize
    def inputStream = jzf.getInputStream(ze)
  }

  type SymLink = Nothing

  def createPath(xs: Array[String]): Path = {
    val parent = createDirectory(xs.init)
    parent.ch(xs.last)
  }

  def createDirectory(xs: Array[String]): Directory = {
    var c = zip.root
    for (x <- xs) c = c.ch(x).asInstanceOf[Directory]
    c
  }

  def createFile(xs: Array[String]): File = createPath(xs).asInstanceOf[File]

  def createSymLink(xs: Array[String]) = throw new UnsupportedOperationException("Zip files do not support symbolic links.")
}

object ZipArchive {

  /**
   * Opens a zip archive from a local file as a new file system.
   * @param enc Encoding of the filenames in the zip archive
   */
  def apply(f: Local.File)(implicit enc: Codec) = new ZipArchive(f, enc)


  def apply(s: String)(implicit enc: Codec) = new ZipArchive(Local.File(s), enc)
}
