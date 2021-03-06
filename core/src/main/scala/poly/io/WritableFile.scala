package poly.io

/**
 * Represents a regular file under a specific file system.
 * @author Tongfei Chen
 * @since 0.2.0
 */
trait WritableFile[S <: WritableFileSystem] extends ReadOnlyFile[S] with WritablePath[S] { self: S#File =>

  /** Opens an output stream to write raw bytes to this file. */
  def outputStream: OutputStream

  def managedOutputStream = Resource(outputStream)

  /** Opens a writer to write characters to this file given a character encoding. */
  def writer(implicit enc: Codec): java.io.Writer =
    new java.io.BufferedWriter(new java.io.OutputStreamWriter(outputStream, enc.charset))

  def managedWriter(implicit enc: Codec) = Resource(writer(enc))

}
