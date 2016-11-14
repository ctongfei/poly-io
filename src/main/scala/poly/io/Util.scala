package poly.io

import scala.collection.mutable

/**
 * @author Tongfei Chen
 */
private[poly] object Util {

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
    val a = mutable.ArrayBuffer[String]()
    val lcp = lcpLength(xs, ys)
    for (x ← xs.drop(lcp)) a += ".."
    for (x ← ys.drop(lcp)) a += x
    a.toArray
  }

  private[poly] def resolve(xs: Array[String], ys: Array[String]) = {
    val a = mutable.ArrayBuffer[String](xs: _*)
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

  /**
   * @author Tongfei Chen
   * TODO: To be replaced with [[poly.collection.search.DepthFirstTreeSearcher]].
   */
  class DepthFirstTreeSearcher[A](s0: A)(t: A => Traversable[A]) extends Iterator[A] {

    private[this] val stack = mutable.ArrayStack(s0)
    private[this] var curr: A = _

    def hasNext = stack.nonEmpty

    def next() = {
      curr = stack.pop()
      t(curr) foreach stack.push
      curr
    }
  }

}
