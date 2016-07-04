package poly.io

import poly.io.conversion._

/**
 * Represents a regular file under a specific file system.
 * @author Tongfei Chen
 * @since 0.2.0
 */
trait File[S <: FileSystem[S]] extends Path[S] { self: S#File =>

  /** Returns the number of bytes in this file. */
  def size: Long

  def inputStream: InputStream

  def outputStream: OutputStream

  def reader(implicit enc: Encoding): java.io.Reader =
    new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, enc.charset))

  def writer(implicit enc: Encoding): java.io.Writer =
    new java.io.BufferedWriter(new java.io.OutputStreamWriter(outputStream, enc.charset))

  def bytes: Iterable[Byte] = new Iterable[Byte] {
    def iterator = FromJava.javaInputStreamAsScalaByteIterator(inputStream)
  }

  def chars(implicit enc: Encoding): Iterable[Char] = new Iterable[Char] {
    def iterator = FromJava.javaReaderAsScalaCharIterator(reader(enc))
  }

  def lines(implicit enc: Encoding): Iterable[String] = new Iterable[String] {
    def iterator = FromJava.javaReaderAsScalaLineIterator(reader(enc))
  }

  def slurp(implicit enc: Encoding): String = {
    val sb = new StringBuilder
    for (c <- chars(enc)) sb append c
    sb.result()
  }


}
