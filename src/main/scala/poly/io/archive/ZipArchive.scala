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
class ZipArchive private(zf: Local.File, enc: Encoding) extends ReadOnlyFileSystem { z =>

  val prefix: String = s"zip:$zf!${zf.fileSystem.separator}"
  def separator = zf.fileSystem.separator

  val root = new Directory(Array())

  private[this] val jzf = new ZipFile(zf.toString, enc.charset)

  private[this] def createDirectory(path: Array[String]): z.Directory = {
    var dir: z.Directory = root
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

  for (ze <- jzf.stream.iterator) {
    val name = ze.getName
    if (name endsWith "/") createDirectory(name.split('/').filter(_ != ""))
    else createFile(name.split('/'), ze)
  }

  sealed abstract class Path extends poly.io.ReadOnlyPath[z.type] {
    val fileSystem: z.type = z
    def permissions = throw new UnsupportedOperationException("Zip archives does not support POSIX permissions")
    def isHidden = permissions
    def isReadable = true
    def isWriteable = false
    def isExecutable = permissions
}

  class Directory(val path: Array[String]) extends Path with poly.io.ReadOnlyDirectory[z.type] {
    private[io] val ch = mutable.HashMap[String, Path]()
    def children: Iterable[Path] = ch.values
    def subdirectories: Iterable[Directory] = ch.values.collect { case d: Directory => d }
    def files: Iterable[File] = ch.values.collect { case f: File => f }
    def /(s: String): Directory = ch(s).asInstanceOf[Directory]
    def /!(s: String): File = ch(s).asInstanceOf[File]
    def /@(s: String): SymLink = throw new UnsupportedOperationException("Zip files do not support symbolic links.")
    def contains(name: String): Boolean = ch contains name
  }

  class File(val path: Array[String], val ze: ZipEntry) extends Path with poly.io.ReadOnlyFile[z.type] {
    def size = ze.getSize
    def inputStream = jzf.getInputStream(ze)
  }

  type SymLink = Nothing

  def directory(xs: Array[String]): Directory = ???

  def file(xs: Array[String]): File = ???

  def symLink(xs: Array[String]): SymLink = ???

}

object ZipArchive {
  def apply(f: Local.File)(implicit enc: Encoding) = new ZipArchive(f, enc)
  def apply(s: String)(implicit enc: Encoding) = new ZipArchive(Local.File(s), enc)
}