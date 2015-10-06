package poly.io

import scala.collection._

import java.io._
import java.nio.file.{Files => JFiles, Paths => JPaths, Path => JPath, _}
import scala.collection._

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
case class File(directory: Directory, name: String) extends BaseFile {

  val j = JPaths.get(fullName)
  def fullName = directory.fullName + FileSystem.separator + name
  require(JFiles.isRegularFile(j), s"$fullName is not a valid file.")

  def fileName = name

  def extension: String = {
    val dotPos = name.lastIndexOf('.')
    if (dotPos == -1) "" else name.substring(dotPos + 1).toLowerCase
  }

  //region Navigation
  def parent = directory

  //endregion

  //region Reading & Writing
  def byteReader: Iterator[Byte] = new Iterator[Byte] {
    val inputStream = new BufferedInputStream(JFiles.newInputStream(j))
    private[this] var nextByte: Int = -1
    def hasNext = {
      if (nextByte != -1) true
      else {
        nextByte = inputStream.read()
        val hasNext = nextByte != -1
        if (!hasNext) inputStream.close()
        hasNext
      }
    }
    def next() = {
      if (nextByte != -1 || hasNext) {
        val byte = nextByte
        nextByte = -1
        byte.toByte
      }
      else throw new NoSuchElementException
    }
  }

  def byteWriter: Observer[Byte] = new Observer[Byte] {
    val writer = new BufferedOutputStream(JFiles.newOutputStream(j))
    def write(x: Byte) = writer.write(x.toInt)
    def close() = writer.close()
  }

  /** Returns a lazy byte sequence of this file. */
  def bytes: Iterable[Byte] = new Iterable[Byte] {
    def iterator = byteReader
  }

  def charReader(implicit encoding: Encoding): Iterator[Char] = new Iterator[Char] {
    val reader = JFiles.newBufferedReader(j, encoding.charset)
    private[this] var nextChar: Int = -1

    def hasNext = {
      if (nextChar != -1) true
      else {
        nextChar = reader.read()
        val hasNext = nextChar != -1
        if (!hasNext) reader.close()
        hasNext
      }
    }
    def next() = {
      val char = nextChar
      nextChar = -1
      char.toChar
    }
  }

  def charWriter(implicit encoding: Encoding): Observer[Char] = new Observer[Char] {
    val writer = JFiles.newBufferedWriter(j, encoding.charset)
    def write(x: Char) = writer.write(x.toInt)
    def close() = writer.close()
  }

  /** Returns a lazy character sequence of this file. */
  def chars(implicit encoding: Encoding): Iterable[Char] = new Iterable[Char] {
    def iterator = charReader
  }

  def lineReader(implicit encoding: Encoding): Iterator[String] = new Iterator[String] {
    val reader = JFiles.newBufferedReader(j, encoding.charset)
    private[this] var nextLine: String = null

    def hasNext = {
      if (nextLine != null) true
      else {
        nextLine = reader.readLine()
        val hasNext = nextLine != null
        if (!hasNext) reader.close()
        hasNext
      }
    }

    def next() = {
      if ((nextLine != null) || hasNext) {
        val line = nextLine
        nextLine = null
        line
      }
      else throw new NoSuchElementException
    }
  }

  def lineWriter(implicit encoding: Encoding): Observer[String] = new Observer[String] {
    val writer = JFiles.newBufferedWriter(j, encoding.charset)
    def write(l: String) = writer.write(l)
    def close() = writer.close()
  }

  /** Returns a lazy line sequence of this file. */
  def lines(implicit encoding: Encoding): Iterable[String] = new Iterable[String] {
    def iterator = lineReader
  }

  //endregion

  //region cp, mv, rm
  def remove(): Unit = {
    JFiles.delete(j)
  }

  def moveTo(dir: Directory): Unit = JFiles.move(j, dir.j.resolve(name))

  def moveToOverwrite(dir: Directory): Unit = JFiles.move(j, dir.j.resolve(name), StandardCopyOption.REPLACE_EXISTING)

  def renameTo(newName: String): Unit = {
    JFiles.move(j, j.resolveSibling(newName))
  }

  def copyTo(dir: Directory): Unit = JFiles.copy(j, dir.j.resolve(name))


  override def toString = fullName


}

object File {

  def apply(s: String): File = {
    fromJavaPath(JPaths.get(s))
  }

  def fromJavaPath(j: JPath): File = {
    require(j.toString.startsWith(FileSystem.prefix))
    val tokens = j.toString.substring(FileSystem.prefix.length).split(FileSystem.separator)
    File(Directory(tokens.init), tokens.last)
  }
}
