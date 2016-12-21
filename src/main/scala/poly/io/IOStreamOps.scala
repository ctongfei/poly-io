package poly.io

import java.util.zip._

import poly.io.compression._

/**
 * @author Tongfei Chen
 * @since 0.3.2
 */
trait IOStreamOps {

  implicit class InputStreamOps(val is: InputStream) extends Iterator[Byte] {

    private[this] var nextByte: Int = -1

    def hasNext = {
      if (nextByte != -1) true
      else {
        nextByte = is.read()
        val hasNext = nextByte != -1
        if (!hasNext) is.close()
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

    /**
     * Decompresses this input stream using a decompressor.
     */
    def decompress(d: Decompressor) = d decompress is

    /**
     * Decodes this input stream using a specific codec.
     */
    def decode(implicit codec: Codec) = codec decode is

  }

  implicit class OutputStreamOps(val os: OutputStream) {

    /**
     * Compresses this output stream using a compressor.
     */
    def compress(c: Compressor) = c compress os

    /**
     * Encodes this output stream using a specific codec.
     */
    def encode(implicit codec: Codec) = codec encode os

  }

}
