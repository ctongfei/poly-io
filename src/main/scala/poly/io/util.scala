package poly.io

import scala.collection.mutable._

/**
 * @author Tongfei Chen
 */
private[poly] object util {

  private[poly] def lcpLength(xs: Array[String], ys: Array[String]) = {
    var i = 0
    var s = 0
    while (i < math.min(xs.length, ys.length)) {
      if (xs(i) == ys(i)) s += 1
      i += 1
    }
    s
  }

  private[poly] def relativize(xs: Array[String], ys: Array[String]) = {
    val a = ArrayBuffer[String]()
    val lcp = lcpLength(xs, ys)
    for (x ← xs.drop(lcp)) a += ".."
    for (x ← ys.drop(lcp)) a += x
    a.toArray
  }

  private[poly] def resolve(xs: Array[String], ys: Array[String]) = {
    val a = ArrayBuffer[String](xs: _*)
    for (x ← ys) {
      x match {
        case ".." => a.remove(a.size - 1)
        case "." => /* do nothing */
        case _ => a += x
      }
    }
    a.toArray
  }

  private[poly] def withResource[R <: java.io.Closeable](r: => R, f: R => Any) = {
    val resource = r
    f(resource)
    resource.close()
  }

}
