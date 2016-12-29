package poly.io

/**
 * @author Tongfei Chen
 * @since 0.4.0
 */
abstract class StreamAsIterator[A, B](val eosSymbol: B) extends Iterator[A] {

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


abstract class StreamAsCloseableIterator[A, B](val eosSymbol: B) extends CloseableIterator[A] {

  private[this] var nextItem: B = eosSymbol

  def convert(b: B): A

  def read(): B
  def close(): Unit

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

