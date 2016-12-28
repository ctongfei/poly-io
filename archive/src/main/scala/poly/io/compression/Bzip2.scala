package poly.io.compression

import org.apache.commons.compress.compressors.bzip2._
import poly.io._

/**
 * @author Tongfei Chen
 * @since 0.4.0
 */
object Bzip2 extends Compressor with Decompressor {
  def compress(os: OutputStream) = new BZip2CompressorOutputStream(os)
  def decompress(is: InputStream) = new BZip2CompressorInputStream(is)
}
