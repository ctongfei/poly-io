package poly.io

import java.nio.charset._

/**
 * A wrapper for `java.nio.charset.Charset` that represents a codec that can encode
 * and decode between a sequence of characters and their encoded byte sequence.
 * @author Tongfei Chen
 * @since 0.1.0
 */
class Codec private(val charset: Charset) {

  def decode(in: java.nio.ByteBuffer) = charset.decode(in)
  def encode(out: java.nio.CharBuffer) = charset.encode(out)

  def decode(in: InputStream): Reader = new java.io.InputStreamReader(in, charset)
  def encode(out: OutputStream): Writer = new java.io.OutputStreamWriter(out, charset)

}

object Codec {

  def apply(name: String) = new Codec(Charset.forName(name))

  def apply(charset: Charset) = new Codec(charset)

  implicit object UTF8 extends Codec(StandardCharsets.UTF_8)

  object UTF16 extends Codec(StandardCharsets.UTF_16)
  object ASCII extends Codec(StandardCharsets.US_ASCII)
  object Default extends Codec(Charset.defaultCharset())
  object ISOLatin1 extends Codec(StandardCharsets.ISO_8859_1)

}
