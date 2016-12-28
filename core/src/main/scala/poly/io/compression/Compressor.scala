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

trait Decompressor {

  /** Decompresses this input stream. */
  def decompress(is: InputStream): InputStream

}
