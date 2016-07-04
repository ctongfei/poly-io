package poly.io

import java.nio.file.{Files => JFiles, Path => JPath, Paths => JPaths}
import scala.collection.JavaConversions._

/**
 * Represents the local file system.
 * @author Tongfei Chen
 * @since 0.2.0
 */
sealed trait Local extends FileSystem[Local] { self: Local =>

  val prefix = if (System.getProperty("os.name").startsWith("Windows")) "" else "/"
  val separator = if (System.getProperty("os.name").startsWith("Windows")) "\\" else "/"

  sealed abstract class Path extends poly.io.Path[Local] {
    val fileSystem = Local
    lazy val jp: JPath = JPaths.get(toString)
    // PERMISSIONS
    def permissions = JFiles.getPosixFilePermissions(jp)
    def isReadable = JFiles.isReadable(jp)
    def isWriteable = JFiles.isWritable(jp)
    def isExecutable = JFiles.isExecutable(jp)

    def delete() = this match {
      case self: Directory =>
        for (c <- self.children) c.delete()
        JFiles.delete(jp)
      case _ => JFiles.delete(jp)
    }
  }

  class File(val path: Array[String]) extends Path with poly.io.File[Local] {
    def size = JFiles.size(jp)
    def inputStream = JFiles.newInputStream(jp)
    def outputStream = JFiles.newOutputStream(jp)
  }

  object File {
    def apply(s: String) = j2pf(JPaths.get(s))
  }

  class Directory(val path: Array[String]) extends Path with poly.io.Directory[Local] {

    def children = JFiles.list(jp).iterator().toIterable.map(j2pp)
    def subdirectories = JFiles.list(jp).iterator().toIterable.filter(f => JFiles.isDirectory(f)).map(j2pd)
    def files = JFiles.list(jp).iterator().toIterable.filter(f => JFiles.isRegularFile(f)).map(j2pf)
    def recursiveSubdirectories = JFiles.walk(jp).iterator().toIterable.filter(f => JFiles.isDirectory(f)).map(j2pd)
    def recursiveFiles = JFiles.walk(jp).iterator().toIterable.filter(f => JFiles.isRegularFile(f)).map(j2pf)
    def /(s: String): Directory = new Directory(path :+ s)
    def /!(s: String): File = new File(path :+ s)
    def /@(s: String): SymLink = new SymLink(path :+ s)
    def exists(name: String) = JFiles.exists(new File(path :+ name).jp)
    def createDirectory(name: String) = j2pd(JFiles.createDirectory(new Directory(path :+ name).jp))
    def createFile(name: String) = j2pf(JFiles.createFile(new File(path :+ name).jp))
    def createSymLink(name: String, target: Local#Path) = j2pl(JFiles.createSymbolicLink(new SymLink(path :+ name).jp, target.jp))
  }

  object Directory {
    def apply(s: String): Directory = {
      if (s.startsWith("~"))
        j2pd(JPaths.get(home.fullName + s.substring(1)))
      else j2pd(JPaths.get(s))
    }
    /** Returns the root of the local filesystem. */
    lazy val root = Directory(prefix)
    /** Returns the home folder of the current user of the local filesystem. */
    lazy val home = Directory(System.getProperty("user.home"))
    /** Returns the current working directory. */
    lazy val cwd = Directory(System.getProperty("user.dir"))
  }

  class SymLink(val path: Array[String]) extends Path with poly.io.SymLink[Local] {
    def target = j2pd(JFiles.readSymbolicLink(jp))
  }

  private def p2j(p: Path) = JPaths.get(p.fullName)

  private def j2pf(p: JPath) = {
    val s = p.normalize().toString
    new File(s.substring(prefix.length).split(separator))
  }

  private def j2pd(p: JPath) = {
    val s = p.normalize().toString
    new Directory(s.substring(prefix.length).split(separator))
  }

  private def j2pl(p: JPath) = {
    val s = p.normalize().toString
    new SymLink(s.substring(prefix.length).split(separator))
  }

  private def j2pp(p: JPath) = {
    if (JFiles.isRegularFile(p)) j2pf(p)
    else if (JFiles.isDirectory(p)) j2pd(p)
    else j2pl(p)
  }

  def root = Directory.root
  def directory(xs: Array[String]) = new Directory(xs)
  def file(xs: Array[String]) = new File(xs)
  def symLink(xs: Array[String]) = new SymLink(xs)
}


// Local#Path and Local.Path is actually the same type: there is only one instance of the trait Local
// Should be able to write {{{
//   object Local extends FileSystem[Local.type]
// }}}
// However due to a compiler bug this is not currently possible.
// Blocking on SI-9844: https://issues.scala-lang.org/browse/SI-9844
// See http://stackoverflow.com/questions/32203867/scala-f-bounded-polymorphism-on-object.
// See https://github.com/scala/scala/commit/ca4c5020d3d2e8e843a44279cbcf9585931cb26f.
object Local extends Local {

  implicit def conv(p: Local#Path): Local.Path = p.asInstanceOf[Local.Path]

  implicit object FileTransferring extends FileTransferring[Local, Local] {

    def copyTo(f: Local#Path, d: Local#Directory) = f match {
      case f: Local.Directory =>
        for (c <- f.children) copyTo(c, d / f.name)
      case _ =>
        JFiles.copy(f.jp, d.jp.resolve(f.name))
    }

    def moveTo(f: Local#Path, d: Local#Directory) = JFiles.move(f.jp, d.jp.resolve(f.name))
  }

}
