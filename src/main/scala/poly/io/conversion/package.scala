package poly.io

import scala.language.implicitConversions

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
package object conversion {

  implicit def javaCharsetAsPoly(jcs: java.nio.charset.Charset): Encoding = new Encoding(jcs)

}
