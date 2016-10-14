package poly.io.util

import scala.collection.mutable

/**
 * @author Tongfei Chen
 * TODO: To be replaced with [[poly.collection.search.DepthFirstTreeSearcher]].
 */
class DepthFirstTreeSearcher[A](s0: A)(t: A => Traversable[A]) extends Iterator[A] {

  private[this] val stack = mutable.Stack(s0)
  private[this] var curr: A = _

  def hasNext = stack.nonEmpty

  def next() = {
    curr = stack.pop()
    t(curr) foreach stack.push
    curr
  }
}
