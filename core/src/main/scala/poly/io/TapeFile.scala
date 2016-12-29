package poly.io

/**
 * @author Tongfei Chen
 * @since 0.4.0
 */
trait TapeFile[S <: TapeFileSystem] { self: S#File =>

  /** Returns the number of bytes in this file. */
  def size: Long

  /** Opens an input stream to read raw bytes from this file. */
  def inputStream: InputStream

  /** Opens a reader to read characters from this file given a character encoding. */
  def reader(implicit enc: Codec): java.io.Reader =
    new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, enc.charset))

  /** Returns a lazy iterable sequence of raw bytes in this file. */
  def bytes: AutoCloseableIterable[Byte] = new AutoCloseableIterable[Byte] {
    def iterator = inputStream.asCloseableIterator
  }

  /** Returns a lazy iterable sequence of characters in this file given a character encoding. */
  def chars(implicit enc: Codec): AutoCloseableIterable[Char] = new AutoCloseableIterable[Char] {
    def iterator = reader(enc).asCloseableIterator
  }

  /** Returns a lazy iterable sequence of lines in this file given a character encoding. */
  def lines(implicit enc: Codec): AutoCloseableIterable[String] = new AutoCloseableIterable[String] {
    def iterator = reader(enc).linesIterator
  }

  /** Reads all content of this file to a string. */
  def slurp(implicit enc: Codec): String = {
    val sb = new StringBuilder
    for (c <- chars(enc)) sb append c
    sb.result()
  }

  final def isDirectory: Boolean = false
  final def isFile: Boolean = true
  final def isSymLink: Boolean = false

}
