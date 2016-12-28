package poly.io

import java.io._
import scala.collection._

/**
 * @author Tongfei Chen
 * @since 0.3.2
 */
trait ReaderWriterOps {

  implicit class ReaderOps(val reader: Reader) extends Iterator[Char] {

    private[this] var nextChar: Int = -1

    def hasNext = {
      if (nextChar != -1) true
      else {
        nextChar = reader.read()
        val hasNext = nextChar != -1
        if (!hasNext) reader.close()
        hasNext
      }
    }

    def next() = {
      if ((nextChar != -1) || hasNext) {
        val char = nextChar
        nextChar = -1
        char.toChar
      }
      else throw new NoSuchElementException
    }

    def linesIterator: Iterator[String] = new Iterator[String] {
      private[this] val br = new BufferedReader(reader)
      private[this] var nextLine: String = _
      def hasNext = {
        if (nextLine != null) true
        else {
          nextLine = br.readLine()
          val hasNext = nextLine != null
          if (!hasNext) br.close()
          hasNext
        }
      }
      def next() = {
        if ((nextLine != null) || hasNext) {
          val line = nextLine
          nextLine = null
          line
        }
        else throw new NoSuchElementException
      }
    }

  }

  implicit class WriterOps(val writer: Writer) extends mutable.Builder[Char, Unit] {
    def +=(elem: Char) = { writer.write(elem); this }
    def clear() = throw new Exception("Writers cannot be cleared.")
    def result() = writer.close()

    def linesBuilder: mutable.Builder[String, Unit] = new mutable.Builder[String, Unit] {
      private[this] val bw = new BufferedWriter(writer)
      def +=(elem: String) = { writer.write(elem + System.lineSeparator()); this }
      def clear() = throw new Exception("Writers cannot be cleared.")
      def result() = writer.close()
    }
  }

}
