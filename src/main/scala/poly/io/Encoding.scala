package poly.io

import java.nio.charset._

/**
 * A wrapper for `java.nio.charset.Charset` that represents a codec that can encode
 * and decode between a sequence of characters and their encoded byte sequence.
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
class Encoding(val charset: Charset)

object Encoding {

  def apply(name: String) = new Encoding(Charset.forName(name))

  implicit object UTF8 extends Encoding(StandardCharsets.UTF_8)

  object UTF16 extends Encoding(StandardCharsets.UTF_16)

  object ASCII extends Encoding(StandardCharsets.US_ASCII)

  object Default extends Encoding(Charset.defaultCharset())

  object ISOLatin1 extends Encoding(StandardCharsets.ISO_8859_1)

}
