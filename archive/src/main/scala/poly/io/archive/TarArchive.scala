package poly.io.archive

import poly.io._
import org.apache.commons.compress.archivers.tar._

class TarArchive private(inputStream: => InputStream) extends TapeFileSystem { tar =>
  def prefix = "/"
  def separator = "/"

  abstract class Path private[io](private[this] val te: TarArchiveEntry) extends TapePath[this.type] {
    val fileSystem: tar.type = tar
    def path = te.getName.split("/")
    def permissions = ??? //TODO: getMode
    def isHidden = ???
    def isReadable = ???
    def isWriteable = ???
    def isExecutable = ???
}

  class File private[io](te: TarArchiveEntry, is: InputStream) extends Path(te) with TapeFile[this.type] {
    def size = te.getSize
    def inputStream = new java.io.InputStream { // suppress the closing of the input stream!
      def read() = is.read()
      override def read(b: Array[Byte], off: Int, len: Int) = is.read(b, off, len)
    }
  }

  class Directory private[io](te: TarArchiveEntry) extends Path(te) with TapeDirectory[this.type]

  class SymLink private[io](te: TarArchiveEntry) extends Path(te) with TapeSymLink[this.type]

  def paths: AutoCloseableIterable[Path] = new AutoCloseableIterable[Path] {
    private[this] val ti = new TarIterator(inputStream)
    def iterator = ti.map {
      case te if te.isDirectory => new Directory(te)
      case te if te.isFile => new File(te, ti.tarStream)
      case te if te.isSymbolicLink => new SymLink(te)
    }
  }

  def files: AutoCloseableIterable[File] = new AutoCloseableIterable[File] {
    private[this] val ti = new TarIterator(inputStream)
    def iterator = ti.collect {
      case te if te.isFile => new File(te, ti.tarStream)
    }
  }
}

object TarArchive {

  /** Constructs a TAR archive from any readable file. */
  def apply(tar: TapeFile[_]) = new TarArchive(tar.inputStream)

  /** Constructs a TAR archive from a local file path string. */
  def apply(tarFilename: String) = new TarArchive(Local.File(tarFilename).inputStream)

  /** Constructs a TAR archive from an input stream. */
  def apply(inputStream: => InputStream) = new TarArchive(inputStream)

}

private[io] class TarIterator(tarInputStream: InputStream) extends StreamAsCloseableIterator[TarArchiveEntry, TarArchiveEntry](null) {
  private[io] val tarStream = new TarArchiveInputStream(tarInputStream)
  def convert(b: TarArchiveEntry) = b
  def read() = tarStream.getNextTarEntry
  def close() = tarStream.close()
}
