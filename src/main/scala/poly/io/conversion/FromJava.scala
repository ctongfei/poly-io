package poly.io.conversion

import java.io._
import scala.collection._
import scala.collection.JavaConversions._
import scala.language.implicitConversions
import poly.io._
import poly.io.conversion.ImplicitlyFromJava._

/**
 * @author Tongfei Chen
 */
object FromJava {

  implicit class javaStreamAsScalaIterable[T](val juss: java.util.stream.Stream[T]) extends AnyVal {
    def asIterable: Iterable[T] = new AbstractIterable[T] {
      def iterator = juss.iterator()
    }
  }

  implicit class JavaFileAsPolyOps(val jif: java.io.File) extends AnyVal {
    def asPolyPath = javaFileAsPoly(jif)
  }

  implicit class JavaPathAsPolyOps(val jnfp: java.nio.file.Path) extends AnyVal {
    def asPolyPath = javaPathAsPoly(jnfp)
  }

  implicit class JavaCharsetAsPolyOps(val jcs: java.nio.charset.Charset) extends AnyVal {
    def asPolyCodec = javaCharsetAsPoly(jcs)
  }

}
