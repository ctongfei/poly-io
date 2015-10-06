import poly.io._
import poly.algebra.function._
import poly.algebra.ops._

Directory("/Users/tongfei/")
File("/Users/tongfei/my/proj/sbt/poly-algebra/build.sbt")

val a = Directory("/Users/tongfei/my/acad")
val b = Directory("/Users/tongfei/my/proj/")

val ab = a relativize b
val ba = b relativize a

a / ab == b
(b / ba) == a


sup(a, b)

a < b

(a / "techo") < a


val h = Directory("/Users/tongfei/my/proj/sbt/poly-io/test/")
h.subdirectories
h.recursiveSubdirectories
h.files
h.recursiveFiles.filterNot(_.isHidden)

