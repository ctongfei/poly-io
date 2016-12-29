package poly.io.compression

import poly.io._

/**
 * @author Tongfei Chen
 * @since 0.3.3
 */
trait Compressor {

  /** Compresses this output stream. */
  def compress(os: OutputStream): OutputStream

}

object Compressor {
  object Id extends Compressor {
    def compress(os: OutputStream) = os
  }
}

trait Decompressor {

  /** Decompresses this input stream. */
  def decompress(is: InputStream): InputStream

}

object Decompressor {
  object Id extends Decompressor {
    def decompress(is: InputStream) = is
  }
}