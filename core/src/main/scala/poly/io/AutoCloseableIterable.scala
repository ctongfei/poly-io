package poly.io

import scala.collection.generic._
import scala.collection._

/**
 * Represents an iterable whose generated iterator is closeable ([[poly.io.CloseableIterator]]).
 * All transformations and actions on this iterable will be auto-closeable.
 * @since 0.4.0
 */
trait AutoCloseableIterable[+A] extends Iterable[A] {

  def iterator: CloseableIterator[A]

  def autoClose[R](f: CloseableIterator[A] => R) = {
    val ci = iterator
    val r = f(ci)
    ci.close()
    r
  }

  //TODO: override members from [[TraversableOnce]]! e.g. map/flatMap
  override def foreach[B](f: A => B) = autoClose(_ foreach f)
  override def forall(p: A => Boolean) = autoClose(_ forall p)
  override def exists(p: A => Boolean) = autoClose(_ exists p)
  override def find(p: A => Boolean) = autoClose(_ find p)
  override def isEmpty = autoClose(!_.hasNext)
  override def foldRight[B](z: B)(op: (A, B) => B) = autoClose(_.foldRight(z)(op))
  override def reduceRight[B >: A](op: (A, B) => B) = autoClose(_.reduceRight(op))
  override def head = autoClose(_.next())
  override def slice(from: Int, until: Int) = {
    val lo = math.max(from, 0)
    val elems = until - lo
    val b = newBuilder
    if (elems <= 0) b.result()
    else {
      b.sizeHintBounded(elems, this)
      var i = 0
      val it = iterator drop lo
      while (i < elems && it.hasNext) {
        b += it.next
        i += 1
      }
      it.close()
      b.result()
    }
  }
  override def take(n: Int) = {
    val b = newBuilder
    if (n <= 0) b.result()
    else {
      b.sizeHintBounded(n, this)
      var i = 0
      val it = iterator
      while (i < n && it.hasNext) {
        b += it.next
        i += 1
      }
      it.close()
      b.result()
    }
  }
  override def drop(n: Int) = {
    val b = newBuilder
    val lo = math.max(0, n)
    b.sizeHint(this, -lo)
    var i = 0
    val it = iterator
    while (i < n && it.hasNext) {
      it.next()
      i += 1
    }
    b ++= it
    it.close()
    b.result()
  }

  override def takeWhile(p: A => Boolean): Iterable[A] = {
    val b = newBuilder
    val it = iterator
    while (it.hasNext) {
      val x = it.next()
      if (!p(x)) return b.result()
      b += x
    }
    it.close()
    b.result()
  }

  override def grouped(size: Int) = {
    for (xs <- iterator grouped size) yield {
      val b = newBuilder
      b ++= xs
      b.result()
    }
  }

  override def sliding(size: Int) = sliding(size, 1)

  override def sliding(size: Int, step: Int) =
    for (xs <- iterator.sliding(size, step)) yield {
      val b = newBuilder
      b ++= xs
      b.result()
    }

  override def takeRight(n: Int) = {
    val b = newBuilder
    b.sizeHintBounded(n, this)
    val lead = this.iterator drop n
    val it = this.iterator
    while (lead.hasNext) {
      lead.next()
      it.next()
    }
    while (it.hasNext) b += it.next()
    lead.close()
    it.close()
    b.result()
  }

  override def dropRight(n: Int) = {
    val b = newBuilder
    if (n >= 0) b.sizeHint(this, -n)
    val lead = iterator drop n
    val it = iterator
    while (lead.hasNext) {
      b += it.next
      lead.next()
    }
    lead.close()
    it.close()
    b.result()
  }

  override def copyToArray[B >: A](xs: Array[B], start: Int, len: Int) {
    var i = start
    val end = (start + len) min xs.length
    val it = iterator
    while (i < end && it.hasNext) {
      xs(i) = it.next()
      i += 1
    }
    it.close()
  }

  override def zip[A1 >: A, B, That](that: GenIterable[B])(implicit bf: CanBuildFrom[Iterable[A], (A1, B), That]): That = {
    val b = bf(repr)
    val these = this.iterator
    val those = that.iterator
    while (these.hasNext && those.hasNext)
      b += ((these.next(), those.next()))
    these.close()
    if (those.isInstanceOf[CloseableIterator[B]]) those.asInstanceOf[CloseableIterator[B]].close()
    b.result()
  }

  override def zipAll[B, A1 >: A, That](that: GenIterable[B], thisElem: A1, thatElem: B)(implicit bf: CanBuildFrom[Iterable[A], (A1, B), That]): That = {
    val b = bf(repr)
    val these = this.iterator
    val those = that.iterator
    while (these.hasNext && those.hasNext)
      b += ((these.next(), those.next()))
    while (these.hasNext)
      b += ((these.next(), thatElem))
    while (those.hasNext)
      b += ((thisElem, those.next()))
    these.close()
    if (those.isInstanceOf[CloseableIterator[B]]) those.asInstanceOf[CloseableIterator[B]].close()
    b.result()
  }

  override def sameElements[B >: A](that: GenIterable[B]): Boolean = {
    val these = this.iterator
    val those = that.iterator
    while (these.hasNext && those.hasNext)
      if (these.next != those.next)
        return false
    val r = !these.hasNext && !those.hasNext
    these.close()
    if (those.isInstanceOf[CloseableIterator[B]]) those.asInstanceOf[CloseableIterator[B]].close()
    r
  }

}