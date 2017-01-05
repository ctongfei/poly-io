package poly.io

import java.io._
import scala.collection._

/**
 * @author Tongfei Chen
 * @since 0.3.2
 */
trait ReaderWriterOps {

  implicit class ReaderOps(val reader: Reader) extends StreamAsIterator[Char, Int](-1) {

    def convert(b: Int) = b.toChar
    def read() = reader.read()

    def linesIterator: Iterator[String] = {
      val br = new BufferedReader(reader)
      new StreamAsIterator[String, String](null) {
        def convert(b: String) = b
        def read() = br.readLine()
        def close() = br.close()
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
