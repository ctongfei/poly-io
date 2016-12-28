package poly.io.conversion

import poly.io._

/**
 * @author Tongfei Chen
 */
object ToJava {

  implicit class LocalPathAsJava(val pp: Local.Path) extends AnyVal {

    /** Casts this instance to a [[java.io.File]]. */
    def asJavaFile = new java.io.File(pp.fullName)

    /** Casts this instance to a [[java.nio.file.Path]]. */
    def asJavaPath = pp.jp

  }

}
