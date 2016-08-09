package poly.io

import poly.io.conversion._

/**
 * Represents a regular file under a specific file system.
 * @author Tongfei Chen
 * @since 0.2.0
 */
trait File[S <: FileSystem] extends Path[S] { self: S#File =>

  /** Returns the number of bytes in this file. */
  def size: Long

  /** Opens an input stream to read raw bytes from this file. */
  def inputStream: InputStream

  /** Opens an output stream to write raw bytes to this file. */
  def outputStream: OutputStream

  /** Opens a reader to read characters from this file given a character encoding. */
  def reader(implicit enc: Encoding): java.io.Reader =
    new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, enc.charset))

  /** Opens a writer to write characters to this file given a character encoding. */
  def writer(implicit enc: Encoding): java.io.Writer =
    new java.io.BufferedWriter(new java.io.OutputStreamWriter(outputStream, enc.charset))

  /** Returns a lazy iterable sequence of raw bytes in this file. */
  def bytes: Iterable[Byte] = new Iterable[Byte] {
    def iterator = FromJava.javaInputStreamAsScalaByteIterator(inputStream)
  }

  /** Returns a lazy iterable sequence of characters in this file given a character encoding. */
  def chars(implicit enc: Encoding): Iterable[Char] = new Iterable[Char] {
    def iterator = FromJava.javaReaderAsScalaCharIterator(reader(enc))
  }

  /** Returns a lazy iterable sequence of lines in this file given a character encoding. */
  def lines(implicit enc: Encoding): Iterable[String] = new Iterable[String] {
    def iterator = FromJava.javaReaderAsScalaLineIterator(reader(enc))
  }

  /** Reads all content of this file to a string. */
  def slurp(implicit enc: Encoding): String = {
    val sb = new StringBuilder
    for (c <- chars(enc)) sb append c
    sb.result()
  }


}
