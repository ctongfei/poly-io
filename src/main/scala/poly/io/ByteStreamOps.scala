package poly.io

import java.util.zip._

/**
 * @author Tongfei Chen
 * @since 0.3.2
 */
trait ByteStreamOps {

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
     * Decompresses this input stream using GZip.
     */
    def decompressGz = new GZIPInputStream(is)

    /**
     * Decodes this input stream using a specific codec.
     */
    def decode(implicit codec: Codec) = codec decode is

  }

  implicit class OutputStreamOps(val os: OutputStream) {

    /**
     * Compresses this output stream using GZip.
     */
    def compressGz = new GZIPOutputStream(os)

    /**
     * Encodes this output stream using a specific codec.
     */
    def encode(implicit codec: Codec) = codec encode os

  }

}

