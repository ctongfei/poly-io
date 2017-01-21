package poly.io

import scala.collection._

/**
 * Converts a Java InputStream/Reader-type object to an iterator.
 * @tparam A Type of elements to be read from the stream
 * @tparam B Type of what [[read()]] function returns
 * @param eosSymbol An object of type [[B]], when encountered, signifies the end of the stream.
 * @author Tongfei Chen
 * @since 0.4.0
 */
abstract class StreamAsIterator[@specialized(Byte, Char) A, @specialized(Int) B](val eosSymbol: B) extends AbstractIterator[A] {

  private[this] var nextItem: B = eosSymbol

  def convert(b: B): A

  def read(): B

  def hasNext = {
    if (nextItem != eosSymbol) true
    else {
      nextItem = read()
      val hasNext = nextItem != eosSymbol
      hasNext
    }
  }

  def next() = {
    if ((nextItem != eosSymbol) || hasNext) {
      val item = nextItem
      nextItem = eosSymbol
      convert(item)
    }
    else throw new NoSuchElementException
  }

}

