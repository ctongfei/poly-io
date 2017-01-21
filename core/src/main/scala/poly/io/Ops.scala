package poly.io

import java.io._
import poly.io.compression._
import scala.collection._

/**
 * @author Tongfei Chen
 */
trait Ops {

  implicit class PipingOps[A](val i: TraversableOnce[A]) {
    def >>(b: mutable.Builder[A, _]) =
      for (x <- i) b += x
  }


  implicit class InputStreamOps(val is: InputStream) extends StreamAsIterator[Byte, Int](-1) {

    def convert(b: Int) = b.toByte
    def read() = is.read()

    /** Decompresses this input stream using a decompressor. */
    def decompress(d: Decompressor) = d decompress is

    /** Decodes this input stream using a specific codec. */
    def decode(implicit codec: Codec) = codec decode is

    /** Buffers this stream. */
    def buffer(size: Int = 8192) = new BufferedInputStream(is, size)

    def >>(os: OutputStream) = {
      for (x <- this) os.write(x)
    }

  }

  implicit class OutputStreamOps(val os: OutputStream) extends mutable.Builder[Byte, Unit] {

    def +=(elem: Byte) = { os.write(elem); this }
    def clear() = throw new Exception("OutputStreams cannot be cleared.")
    def result() = os.close()

    /** Compresses this output stream using a compressor. */
    def compress(c: Compressor) = c compress os

    /** Encodes this output stream using a specific codec. */
    def encode(implicit codec: Codec) = codec encode os

    /** Buffers this stream. */
    def buffer(size: Int = 8192) = new BufferedOutputStream(os, size)

  }
  implicit class ReaderOps(val reader: Reader) extends StreamAsIterator[Char, Int](-1) {

    def convert(b: Int) = b.toChar
    def read() = reader.read()

    def linesIterator: Iterator[String] = {
      val br = new BufferedReader(reader)
      new StreamAsIterator[String, String](null) {
        def convert(b: String) = b
        def read() = br.readLine()
        def close() = br.close()
      }
    }
    def >>(os: Writer) = {
      for (x <- this) os.write(x)
    }

  }

  implicit class WriterOps(val writer: Writer) extends mutable.Builder[Char, Unit] {
    def +=(elem: Char) = { writer.write(elem); this }
    def clear() = throw new Exception("Writers cannot be cleared.")
    def result() = writer.close()

    def linesBuilder: mutable.Builder[String, Unit] = new mutable.Builder[String, Unit] {
      private[this] val bw = new BufferedWriter(writer)
      def +=(elem: String) = { writer.write(elem + System.lineSeparator()); this }
      def clear() = throw new Exception("Writers cannot be cleared.")
      def result() = writer.close()
    }
  }

}
