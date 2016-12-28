package poly.io.conversion

import poly.io._

/**
 * @author Tongfei Chen
 */
object ImplicitlyFromJava {

  implicit def javaFileAsPoly(jif: java.io.File): Local.Path =
    Local.j2pp(java.nio.file.Paths.get(jif.getAbsolutePath))


  implicit def javaPathAsPoly(jnfp: java.nio.file.Path): Local.Path =
    Local.j2pp(jnfp)

  implicit def javaCharsetAsPoly(jcs: java.nio.charset.Charset): Codec =
    Codec(jcs)

}
