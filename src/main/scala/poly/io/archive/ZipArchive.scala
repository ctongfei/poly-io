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
class ZipArchive private[io](zf: Local.File, enc: Encoding) extends ReadOnlyFileSystem { zip =>

  def prefix = s"$zf!${zf.fileSystem.separator}"
  def separator = zf.fileSystem.separator

  /** Returns the root directory of this file system. */
  val root = new Directory(Array())

  private[this] val jzf = new ZipFile(zf.toString, enc.charset)

  private[this] def createDirectory(path: Array[String]): zip.Directory = {
    var dir: zip.Directory = root
    for (p <- path) {
      if (!(dir.ch contains p))
        dir.ch += p -> new Directory(dir.path :+ p)
      dir = dir.ch(p).asInstanceOf[Directory]
    }
    dir
  }

  private[this] def createFile(path: Array[String], ze: ZipEntry): Unit = {
    val dir = createDirectory(path.init)
    dir.ch += path.last -> new File(path, ze)
  }

  // Constructor: construct the tree structure inside this zip archive
  for (ze <- jzf.stream.iterator) {
    val name = ze.getName
    if (name endsWith "/") createDirectory(name.split('/').filter(_ != ""))
    else createFile(name.split('/'), ze)
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

  def getPath(xs: Array[String]): Path = {
    val parent = getDirectory(xs.init)
    parent.ch(xs.last)
  }

  def getDirectory(xs: Array[String]): Directory = {
    var c = zip.root
    for (x <- xs) c = c.ch(x).asInstanceOf[Directory]
    c
  }

  def getFile(xs: Array[String]): File = getPath(xs).asInstanceOf[File]

  def getSymLink(xs: Array[String]) = throw new UnsupportedOperationException("Zip files do not support symbolic links.")
}

object ZipArchive {
  def apply(f: Local.File)(implicit enc: Encoding) = new ZipArchive(f, enc)
  def apply(s: String)(implicit enc: Encoding) = new ZipArchive(Local.File(s), enc)
}
