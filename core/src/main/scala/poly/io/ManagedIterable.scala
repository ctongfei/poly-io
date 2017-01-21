package poly.io

import scala.annotation.unchecked.{uncheckedVariance => uv}
import scala.collection._
import scala.collection.generic._
import scala.reflect._

/**
 * Represents an managed iterable sequence.
 * Allocation of resource will occur when a managed iterable starts iterating.
 * All transformations on a `ManagedIterable` will remain managed, i.e.
 * all resources allocated will be automatically closed.
 * @author Tongfei Chen
 * @since 0.4.0
 */
trait ManagedIterable[@specialized(Char, Byte) +A] { self =>

  def iteratorResource: Resource[Iterator[A]]

  def unsafe(): Iterable[A] = new Iterable[A] {
    def iterator = iteratorResource.open()
  }

  private def mapIterator[B](fi: Iterator[A] => Iterator[B]): ManagedIterable[B] = new AbstractManagedIterable[B] {
    def iteratorResource = self.iteratorResource map fi
  }

  private def applyIterator[B](fi: Iterator[A] => B) = {
    for (it <- iteratorResource) fi(it)
  }

  def foreach[U](f: A => U) = for {
    it <- iteratorResource
    x <- it
  } f(x)

  def map[B](f: A => B) = mapIterator(_ map f)

  def flatMap[B](f: A => Iterable[B]) = mapIterator(_ flatMap f)

  // cannot do flatMap[B](f: A => ManagedIterable[B])! monad composition is not closed!

  def ++[B >: A](that: Iterable[B]) = mapIterator(_ ++ that.iterator)
  def collect[B](pf: PartialFunction[A, B]) = mapIterator(_ collect pf)
  def collectFirst[B](pf: PartialFunction[A, B]) = applyIterator(_ collectFirst pf)
  def count(p: A => Boolean) = applyIterator(_ count p)
  def drop(n: Int) = mapIterator(_ drop n)
  def dropWhile(p: A => Boolean) = mapIterator(_ dropWhile p)
  def exists(p: A => Boolean) = applyIterator(_ exists p)
  def filter(p: A => Boolean) = mapIterator(_ filter p)
  def filterNot(p: A => Boolean) = mapIterator(_ filterNot p)
  def find(p: A => Boolean) = applyIterator(_ find p)
  def fold[B >: A](z: B)(op: (B, B) => B) = applyIterator(_.fold(z)(op))
  def foldLeft[B](z: B)(op: (B, A) => B) = applyIterator(_.foldLeft(z)(op))
  def foldRight[B](z: B)(op: (A, B) => B) = applyIterator(_.foldRight(z)(op))
  def forall(p: A => Boolean) = applyIterator(_ forall p)
  // groupBy
  def grouped(size: Int) = mapIterator(_ grouped size)
  def hasDefiniteSize = applyIterator(_.hasDefiniteSize)
  // head
  // headOption
  // init
  // inits
  // isEmpty
  // last
  // lastOption
  def max[B >: A : Ordering] = applyIterator(_.max)
  def maxBy[B: Ordering](f: A => B) = applyIterator(_ maxBy f)
  def min[B >: A : Ordering] = applyIterator(_.min)
  def minBy[B: Ordering](f: A => B) = applyIterator(_ minBy f)
  def mkString = applyIterator(_.mkString)
  def mkString(sep: String) = applyIterator(_ mkString sep)
  def mkString(start: String, sep: String, end: String) = applyIterator(_.mkString(start, sep, end))
  def nonEmpty = applyIterator(_.hasNext)
  // par
  // partition
  def product[B >: A : Numeric] = applyIterator(_.product)
  def reduce[B >: A](op: (B, B) => B) = applyIterator(_ reduce op)
  def reduceLeft[B >: A](op: (B, A) => B) = applyIterator(_ reduceLeft op)
  def reduceLeftOption[B >: A](op: (B, A) => B) = applyIterator(_ reduceLeftOption op)
  def reduceOption[B >: A](op: (B, B) => B) = applyIterator(_ reduceOption op)
  def reduceRight[B >: A](op: (A, B) => B) = applyIterator(_ reduceRight op)
  def reduceRightOption[B >: A](op: (A, B) => B) = applyIterator(_ reduceRightOption op)
  def sameElements[B >: A](that: GenIterable[B]) = applyIterator(_ sameElements that.iterator)
  def scanLeft[B >: A](z: B)(op: (B, A) => B) = mapIterator(_.scanLeft(z)(op))
  def scanRight[B >: A](z: B)(op: (A, B) => B) = mapIterator(_.scanRight(z)(op))
  def size = applyIterator(_.size)
  def slice(from: Int, until: Int) = mapIterator(_.slice(from, until))
  def sliding(size: Int, step: Int = 1) = mapIterator(_.sliding(size, step))
  // span
  // splitAt
  def stringPrefix = "ManagedIterable"
  def sum[B >: A : Numeric] = applyIterator(_.sum)
  // tail
  // tails
  def take(n: Int) = mapIterator(_ take n)
  def takeWhile(p: A => Boolean) = mapIterator(_ takeWhile p)
  def to[C[_]](implicit cbf: CanBuildFrom[Nothing, A, C[A]]): C[A @uv] = applyIterator(_.to[C])
  def toArray[B >: A : ClassTag]: Array[B] = applyIterator(_.toArray)
  def toBuffer[B >: A]: mutable.Buffer[B] = applyIterator(_.toBuffer)
  def toIndexedSeq = applyIterator(_.toIndexedSeq)
  // toIterable
  // toIterator
  def toList = applyIterator(_.toList)
  def toSeq = applyIterator(_.toSeq)
  def toSet[B >: A]: Set[B] = applyIterator(_.toSet)
  // toStream
  // toString
  // toVector
  def zip[B](that: GenIterable[B]) = mapIterator(_ zip that.iterator)
  def zip[B](that: ManagedIterable[B]): ManagedIterable[(A, B)] = new ManagedIterable[(A, B)] {
    def iteratorResource = (self.iteratorResource productWith that.iteratorResource)(_ zip _)
  }
  def zipAll[B >: A, C](that: GenIterable[C], thisElem: B, thatElem: C) = mapIterator(_.zipAll(that.iterator, thisElem, thatElem))
  def zipAll[B >: A, C](that: ManagedIterable[C], thisElem: B, thatElem: C) = new ManagedIterable[(B, C)] {
    def iteratorResource = (self.iteratorResource productWith that.iteratorResource)(_.zipAll(_, thisElem, thatElem))
  }
  def zipWithIndex = mapIterator(_.zipWithIndex)

}

abstract class AbstractManagedIterable[@specialized(Char, Byte) A] extends ManagedIterable[A]

