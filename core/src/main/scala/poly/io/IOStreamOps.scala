package poly.io

import java.io._

import poly.io.compression._

import scala.collection.mutable

/**
 * @author Tongfei Chen
 * @since 0.3.2
 */
trait IOStreamOps {

  implicit class InputStreamOps(val is: InputStream) {

    def asIterator: Iterator[Byte] = new StreamAsIterator[Byte, Int](-1) {
      def convert(b: Int) = b.toByte
      def read() = is.read()
    }

    def asCloseableIterator: CloseableIterator[Byte] = new StreamAsCloseableIterator[Byte, Int](-1) {
      def convert(b: Int) = b.toByte
      def read() = is.read()
      def close() = is.close()
    }

    /** Decompresses this input stream using a decompressor. */
    def decompress(d: Decompressor) = d decompress is

    /** Decodes this input stream using a specific codec. */
    def decode(implicit codec: Codec) = codec decode is

    /** Buffers this stream. */
    def buffer(size: Int = 8192) = new BufferedInputStream(is, size)

  }

  implicit class OutputStreamOps(val os: OutputStream) extends mutable.Builder[Byte, Unit] {

    def +=(elem: Byte) = { os.write(elem); this }
    def clear() = throw new Exception("Output streams cannot be cleared.")
    def result() = os.close()

    /** Compresses this output stream using a compressor. */
    def compress(c: Compressor) = c compress os

    /** Encodes this output stream using a specific codec. */
    def encode(implicit codec: Codec) = codec encode os

    /** Buffers this stream. */
    def buffer(size: Int = 8192) = new BufferedOutputStream(os, size)

  }

}
