package poly.io.conversion

import java.io._

import poly.io._

import scala.collection.AbstractIterable
import scala.collection.JavaConversions._
import scala.language.implicitConversions

/**
 * @author Tongfei Chen
 */
object FromJava {

  private[io] implicit class javaStreamAsScalaIterable[T](val juss: java.util.stream.Stream[T]) extends AnyVal {
    def asIterable: Iterable[T] = new AbstractIterable[T] {
      def iterator = juss.iterator()
    }
  }

  implicit def javaFileAsPoly(jif: java.io.File): Local.Path = {
    Local.j2pp(java.nio.file.Paths.get(jif.getAbsolutePath))
  }

  implicit def javaPathAsPoly(jnfp: java.nio.file.Path): Local.Path = {
    Local.j2pp(jnfp)
  }

  implicit def javaCharsetAsPoly(jcs: java.nio.charset.Charset): Encoding = new Encoding(jcs)

  def javaInputStreamAsScalaByteIterator(jii: java.io.InputStream): Iterator[Byte] = new Iterator[Byte] {
    private[this] var nextByte: Int = -1
    def hasNext = {
      if (nextByte != -1) true
      else {
        nextByte = jii.read()
        val hasNext = nextByte != -1
        if (!hasNext) jii.close()
        hasNext
      }
    }
    def next() = {
      if ((nextByte != -1) || hasNext) {
        val byte = nextByte
        nextByte = -1
        byte.toByte
      }
      else throw new NoSuchElementException
    }
  }

  def javaReaderAsScalaCharIterator(jir: java.io.Reader): Iterator[Char] = new Iterator[Char] {
    private[this] val reader = new BufferedReader(jir)
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
      if ((nextChar != -1) || hasNext) {
        val char = nextChar
        nextChar = -1
        char.toChar
      }
      else throw new NoSuchElementException
    }
  }

  def javaReaderAsScalaLineIterator(jir: java.io.Reader): Iterator[String] = new Iterator[String] {
    private[this] val reader = new BufferedReader(jir)
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



}
