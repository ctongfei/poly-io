package poly.io.compression

import poly.io._
import org.apache.commons.compress.compressors.bzip2._
import org.apache.commons.compress.compressors.lzma._
import org.apache.commons.compress.compressors.snappy._
import org.apache.commons.compress.compressors.xz._
import org.apache.commons.compress.compressors.z._


/**
 * Encapsulates a Bzip2 compressor/decompressor.
 * @author Tongfei Chen
 * @since 0.4.0
 */
object Bzip2 extends Compressor with Decompressor {
  def compress(os: OutputStream) = new BZip2CompressorOutputStream(os)
  def decompress(is: InputStream) = new BZip2CompressorInputStream(is)

  def withBlockSize(blockSize: Int) = new Compressor {
    def compress(os: OutputStream) = new BZip2CompressorOutputStream(os, blockSize)
  }
}

object Xz extends Compressor with Decompressor {
  def compress(os: OutputStream) = new XZCompressorOutputStream(os)
  def decompress(is: InputStream) = new XZCompressorInputStream(is)

  def withPreset(preset: Int) = new Compressor {
    def compress(os: OutputStream) = new XZCompressorOutputStream(os, preset)
  }
}

object Snappy extends Decompressor { //
  def decompress(is: InputStream) = new SnappyCompressorInputStream(is)
}

object Lzma extends Decompressor { // apache-commons-compress does not provide a compressor for Lzma
  def decompress(is: InputStream) = new LZMACompressorInputStream(is)
}

object Z extends Decompressor { // apache-commons-compress does not provide a compressor
  def decompress(is: InputStream) = new ZCompressorInputStream(is)
}