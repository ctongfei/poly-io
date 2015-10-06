package poly.io

/**
 * A wrapper for `java.nio.charset.Charset` that represents a codec that can encode
 * and decode between a sequence of characters and their encoded byte sequence.
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
class Encoding(val charset: java.nio.charset.Charset)


object Encoding {

  implicit object UTF8 extends Encoding(java.nio.charset.Charset.forName("UTF-8"))

}