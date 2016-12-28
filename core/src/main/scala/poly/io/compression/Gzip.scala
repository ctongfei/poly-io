package poly.io.compression

import java.util.zip._
import poly.io._

/**
 * @author Tongfei Chen
 * @since 0.3.3
 */
object Gzip extends Compressor with Decompressor {
  def compress(os: OutputStream) = new GZIPOutputStream(os)
  def decompress(is: InputStream) = new GZIPInputStream(is)
}
