package poly.io

import poly.algebra._

import java.nio.file.{Files => JFiles, Paths => JPaths, Path => JPath, _}
import scala.collection.JavaConversions._
import scala.collection._

/**
 * Represents an absolute directory on the local file system.
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
case class Directory(path: Array[String]) extends BaseFile {

  val j = JPaths.get(fullName)
  require(JFiles.isDirectory(j), s"$fullName is not a valid directory.")

  def fullName = FileSystem.prefix + path.mkString(FileSystem.separator)

  def name = path.last

  //region Navigation
  def parent = Directory(path.init)

  def subdirectories: Iterable[Directory] = new Iterable[Directory] {
    def iterator = JFiles.newDirectoryStream(j).iterator().filter(d => JFiles.isDirectory(d)).map(Directory.fromJavaPath)
  }

  def recursiveSubdirectories: Iterable[Directory] = new Iterable[Directory] {
    def iterator = JFiles.walk(j).iterator().drop(1).filter(d => JFiles.isDirectory(d)).map(Directory.fromJavaPath)
  }

  def files: Iterable[File] = new Iterable[File] {
    def iterator = JFiles.newDirectoryStream(j).iterator().filter(d => JFiles.isRegularFile(d)).map(File.fromJavaPath)
  }

  def recursiveFiles: Iterable[File] = new Iterable[File] {
    def iterator = JFiles.walk(j).iterator().drop(1).filter(d => JFiles.isRegularFile(d)).map(File.fromJavaPath)
  }

  def /(child: String) = Directory.fromJavaPath(j.resolve(child).normalize())

  def /(rp: RelativePath) = Directory.fromJavaPath(j.resolve(JPaths.get(rp.path.mkString(FileSystem.separator))).normalize())

  def /!(child: String) = File(this, child)

  //endregion

  //region cp, mv, rm
  def existsFile(name: String) = JFiles.exists(j.resolve(name)) && JFiles.isRegularFile(j.resolve(name))

  def existsDirectory(name: String) = JFiles.exists(j.resolve(name)) && JFiles.isDirectory(j.resolve(name))

  def createFile(name: String) = JFiles.createFile(j.resolve(name))

  def createFileIfNotExist(name: String) = if (!existsFile(name)) createFile(name)

  def createDirectory(name: String) = JFiles.createDirectory(j.resolve(name))

  def createDirectoryIfNotExist(name: String) = if (!existsDirectory(name)) createDirectory(name)

  def remove(): Unit = {
    for (c ← subdirectories)
      c.remove()
    for (f ← files)
      f.remove()
  }

  def moveTo(dst: Directory): Unit = {
    JFiles.move(j, dst.j.resolve(name))
  }

  def moveToOverwrite(dst: Directory): Unit = {
    JFiles.move(j, dst.j.resolve(name), StandardCopyOption.REPLACE_EXISTING)
  }

  def renameTo(newName: String): Unit = {
    JFiles.move(j, j.resolveSibling(name))
  }

  def copyTo(dst: Directory): Unit = {
    dst.createDirectoryIfNotExist(this.name)
    for (c ← subdirectories)
      c.copyTo(dst / name)
    for (f ← files)
      f.copyTo(dst / name)
  }

  def copyToOverwrite(dst: Directory): Unit = {
    dst.createDirectoryIfNotExist(this.name)
    for (c ← subdirectories)
      c.copyToOverwrite(dst / name)
    for (f ← files)
      f.copyToOverwrite(dst / name)
  }
  //endregion

  /** Returns the relative path of the destination directory with respect to this directory. */
  def relativize(dst: Directory): RelativePath = {
    val r = j.relativize(dst.j)
    RelativePath(r.toString.split(FileSystem.separator))
  }


  override def equals(that: Any) = that match {
    case that: Directory => this.fullName == that.fullName
    case _ => false
  }
  override def toString = fullName
  override def hashCode = fullName.hashCode
}

object Directory {

  /** Returns the upper semilattice on the set of directories based on the parent/children relation. */
  implicit object UpperSemilattice extends UpperSemilattice[Directory] {
    def sup(x: Directory, y: Directory) = Directory((x.path zip y.path).takeWhile(t => t._1 == t._2).map(_._1))
    override def le(x: Directory, y: Directory) = x.path startsWith y.path
    override def ge(x: Directory, y: Directory) = y.path startsWith x.path
  }

  def apply(s: String): Directory = {
    fromJavaPath(JPaths.get(s))
  }

  def fromJavaPath(j: JPath): Directory = {
    require(j.toString.startsWith(FileSystem.prefix))
    Directory(j.normalize.toString.substring(FileSystem.prefix.length).split(FileSystem.separator))
  }

}