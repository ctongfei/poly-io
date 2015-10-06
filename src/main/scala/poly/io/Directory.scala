package poly.io

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

  def files: Iterable[File] = new Iterable[File] {
    def iterator = JFiles.newDirectoryStream(j).iterator().filter(d => JFiles.isRegularFile(d)).map(File.fromJavaPath)
  }

  def recursiveFiles: Iterable[File] = new Iterable[File] {
    def iterator = JFiles.walk(j).iterator().drop(1).filter(d => JFiles.isRegularFile(d)).map(File.fromJavaPath)
  }

  def /(child: String) = Directory(path :+ child)

  def /!(child: String) = File(this, child)

  //endregion

  //region cp, mv, rm
  def existsFile(name: String) = JFiles.exists(j.resolve(name))

  def create(name: String) = JFiles.createFile(j.resolve(name))

  def createIfNotExist(name: String) = if (!existsFile(name)) create(name)

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
    ???
  }
  //endregion


  override def toString = fullName
  override def hashCode = fullName.##
}

object Directory {

  def apply(s: String): Directory = {
    fromJavaPath(JPaths.get(s))
  }

  def fromJavaPath(j: JPath): Directory = {
    require(j.toString.startsWith(FileSystem.prefix))
    Directory(j.toString.substring(FileSystem.prefix.length).split(FileSystem.separator))
  }

}