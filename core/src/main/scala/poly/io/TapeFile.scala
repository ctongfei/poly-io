package poly.io

/**
 * @author Tongfei Chen
 * @since 0.4.0
 */
trait TapeFile[S <: TapeFileSystem] extends TapePath[S] { self: S#File =>

  /** Returns the number of bytes in this file. */
  def size: Long

  /** Opens an input stream to read raw bytes from this file. */
  def inputStream: InputStream

  /** Safely opens an input stream to read this file in a managed context ([[Resource]]). */
  def managedInputStream = Resource(inputStream)

  /** Opens a reader to read characters from this file given a character encoding. */
  def reader(implicit enc: Codec): java.io.Reader =
    new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, enc.charset))

  /** Safely opens an reader to read this file in a managed context ([[Resource]]). */
  def managedReader(implicit enc: Codec) = for {
    is <- managedInputStream
    r <- Resource(new java.io.BufferedReader(new java.io.InputStreamReader(is, enc.charset)))
  } yield r

  /** Returns a lazy iterable sequence of raw bytes in this file. */
  def bytes: Resource[Iterable[Byte]] = managedInputStream map { is =>
    new Iterable[Byte] {
      def iterator = is
    }
  }

  /** Returns a lazy iterable sequence of characters in this file given a character encoding. */
  def chars(implicit enc: Codec): Resource[Iterable[Char]] = managedReader(enc) map { r =>
    new Iterable[Char] {
      def iterator = r
    }
  }

  /** Returns a lazy iterable sequence of lines in this file given a character encoding. */
  def lines(implicit enc: Codec): Resource[Iterable[String]] = managedReader(enc) map { r =>
    new Iterable[String] {
      def iterator = r.linesIterator
    }
  }

  /** Reads all content of this file to a string. */
  def slurp(implicit enc: Codec): String = {
    val sb = new StringBuilder
    for (is <- managedReader(enc)) {
      var c = 0
      while (c != -1) {
        c = is.read()
        sb += c.toChar
      }
    }
    sb.result()
  }

  final def isDirectory: Boolean = false
  final def isFile: Boolean = true
  final def isSymLink: Boolean = false

}
