package poly.io.archive

import poly.io._
import org.apache.commons.compress.archivers.tar._
import poly.io.compression._

/**
 * Encapsulates a tarball as a tape file system, in which files can be read sequentially.
 * @since 0.4.0
 */
class TarArchive private[io](inputStream: => InputStream) extends TapeFileSystem { tar =>
  def prefix = "/"
  def separator = "/"

  abstract class Path private[io](private[this] val te: TarArchiveEntry) extends TapePath[this.type] {
    val fileSystem: tar.type = tar
    def path = te.getName.split("/")
    def permissions = ??? //TODO: te.getMode
    def isHidden = ???
    def isReadable = ???
    def isWriteable = ???
    def isExecutable = ???
    def modifiedTime = te.getLastModifiedDate.toInstant
  }

  class File private[io](te: TarArchiveEntry, is: InputStream) extends Path(te) with TapeFile[this.type] {
    def size = te.getSize
    def inputStream = new java.io.InputStream { // suppress the closing of the input stream!
      def read() = is.read()                    // closing this input stream would cause the TarArchiveInputStream to be closed!
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

  def directories: AutoCloseableIterable[Directory] = new AutoCloseableIterable[Directory] {
    private[this] val ti = new TarIterator(inputStream)
    def iterator = ti.collect {
      case te if te.isDirectory => new Directory(te)
    }
  }
}

private[io] class TarIterator(tarInputStream: InputStream) extends StreamAsCloseableIterator[TarArchiveEntry, TarArchiveEntry](null) {
  private[io] val tarStream = new TarArchiveInputStream(tarInputStream)
  def convert(b: TarArchiveEntry) = b
  def read() = tarStream.getNextTarEntry
  def close() = tarStream.close()
}

private[io] abstract class TarArchiveFactory(decompressor: Decompressor) {

  /** Constructs a TAR archive from any readable file. */
  def apply(tar: TapeFile[_]) = new TarArchive(decompressor.decompress(tar.inputStream))

  /** Constructs a TAR archive from a local file path string. */
  def apply(tarFilename: String) = new TarArchive(decompressor.decompress(Local.File(tarFilename).inputStream))

  /** Constructs a TAR archive from an input stream. */
  def apply(inputStream: => InputStream) = new TarArchive(decompressor.decompress(inputStream))

}

object TarArchive extends TarArchiveFactory(Decompressor.Id)
object TarGzArchive extends TarArchiveFactory(Gzip)
object TarBz2Archive extends TarArchiveFactory(Bzip2)
object TarXzArchive extends TarArchiveFactory(Xz)

