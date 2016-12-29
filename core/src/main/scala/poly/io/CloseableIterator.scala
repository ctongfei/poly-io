package poly.io

import scala.collection._

/**
 * Represents an iterator with a `close` method that releases resources that it is holding.
 *
 * @author Tongfei Chen
 * @since 0.4.0
 */
trait CloseableIterator[+A] extends Iterator[A] with Closeable { self =>

  private def wrapValue[B](r: => B): B = {
    val result = r
    self.close()
    result
  }

  private def wrapIterator[B](i: Iterator[B]): CloseableIterator[B] = new CloseableIterator[B] {
    def next() = i.next()
    def hasNext = i.hasNext
    def close() = self.close()
  }

  private def wrapIteratorPair[B](p: (Iterator[B], Iterator[B])): (CloseableIterator[B], CloseableIterator[B]) =
    (wrapIterator(p._1), wrapIterator(p._2))

  override def take(n: Int) = wrapIterator { super.take(n) }
  override def drop(n: Int) = wrapIterator { super.drop(n) }
  override def slice(from: Int, until: Int) = wrapIterator { super.slice(from, until) }
  override def map[B](f: (A) => B) = wrapIterator { super.map(f) }
  override def ++[B >: A](that: => GenTraversableOnce[B]) = wrapIterator { super.++(that) }
  override def flatMap[B](f: A => GenTraversableOnce[B]) = wrapIterator { super.flatMap(f) }
  override def filter(p: A => Boolean) = wrapIterator { super.filter(p) }
  override def corresponds[B](that: GenTraversableOnce[B])(p: (A, B) => Boolean) = wrapValue { super.corresponds(that)(p) }
  override def withFilter(p: (A) => Boolean) = wrapIterator { super.withFilter(p) }
  override def filterNot(p: (A) => Boolean) = wrapIterator { super.filterNot(p) }
  override def collect[B](pf: PartialFunction[A, B]) = wrapIterator { super.collect(pf) }
  override def scanLeft[B](z: B)(op: (B, A) => B) = wrapIterator { super.scanLeft(z)(op) }
  override def scanRight[B](z: B)(op: (A, B) => B) = wrapIterator { super.scanRight(z)(op) }
  override def takeWhile(p: (A) => Boolean) = wrapIterator { super.takeWhile(p) }
  override def partition(p: (A) => Boolean) = wrapIteratorPair { super.partition(p) }
  override def span(p: (A) => Boolean) = wrapIteratorPair { super.span(p) }
  override def dropWhile(p: (A) => Boolean) = wrapIterator { super.dropWhile(p) }
  override def zip[B](that: Iterator[B]) = that match {
    case that: CloseableIterator[B] => zipCloseable(that)
    case _ => wrapIterator { super.zip(that) }
  }
  override def padTo[A1 >: A](len: Int, elem: A1) = wrapIterator { super.padTo(len, elem) }
  override def zipWithIndex = wrapIterator { super.zipWithIndex }
  override def zipAll[B, A1 >: A, B1 >: B](that: Iterator[B], thisElem: A1, thatElem: B1) = that match {
    case that: CloseableIterator[B] => zipAllCloseable(that, thisElem, thatElem)
    case _ => wrapIterator { super.zipAll(that, thisElem, thatElem) }
  }

  override def foreach[U](f: (A) => U) = wrapValue { super.foreach(f) }
  override def forall(p: (A) => Boolean) = wrapValue { super.forall(p) }
  override def exists(p: (A) => Boolean) = wrapValue { super.exists(p) }
  override def contains(elem: Any) = wrapValue { super.contains(elem) }
  override def find(p: (A) => Boolean) = wrapValue { super.find(p) }
  override def indexWhere(p: (A) => Boolean) = wrapValue { super.indexWhere(p) }
  override def indexOf[B >: A](elem: B) = wrapValue { super.indexOf(elem) }
  //TODO: override def buffered?

  class CloseableGroupedIterator[B >: A](self: CloseableIterator[A], size: Int, step: Int) extends GroupedIterator[B](self, size, step) with CloseableIterator[Seq[B]] {
    def close() = self.close()
  }

  override def grouped[B >: A](size: Int) = new CloseableGroupedIterator(self, size, size)
  override def sliding[B >: A](size: Int, step: Int) = new CloseableGroupedIterator(self, size, step)
  override def length = wrapValue { super.length }
  //TODO: override def duplicate?
  override def patch[B >: A](from: Int, patchElems: Iterator[B], replaced: Int) = wrapIterator { super.patch(from, patchElems, replaced) }
  override def copyToArray[B >: A](xs: Array[B]) = wrapValue { super.copyToArray(xs) }
  override def sameElements(that: Iterator[_]) = that match {
    case that: CloseableIterator[_] => { val r = super.sameElements(that); self.close(); that.close(); r }
    case _ => wrapValue(super.sameElements(that))
  }
  //TODO: override def toStream

  override def toString() = wrapValue { super.toString() }


  private[this] def superZip[B](that: Iterator[B]) = super.zip(that)
  private[this] def superZipAll[B, A1 >: A, B1 >: B](that: Iterator[B], thisElem: A1, thatElem: B1) = super.zipAll(that, thisElem, thatElem)

  private[this] def zipCloseable[B](that: CloseableIterator[B]) = new CloseableIterator[(A, B)] {
    private[this] val ab = self.superZip(that)
    def hasNext = ab.hasNext
    def next() = ab.next()
    def close() = {
      self.close()
      that.close()
    }
  }

  private[this] def zipAllCloseable[B, A1 >: A, B1 >: B](that: CloseableIterator[B], thisElem: A1, thatElem: B1) = new CloseableIterator[(A1, B1)] {
    private[this] val ab = self.superZipAll(that, thisElem, thatElem)
    def hasNext = ab.hasNext
    def next() = ab.next()
    def close() = {
      self.close()
      that.close()
    }
  }

}
