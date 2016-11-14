package poly.io.archive

import poly.io._

/**
 * Represents the file system inside a JAR archive (Java archive).
 * @author Tongfei Chen
 * @since 0.3.0
 */
// The filenames in JARs must be UTF8: see the spec at http://docs.oracle.com/javase/7/docs/technotes/guides/jar/jar.html.
class JarArchive(jarFile: Local.File) extends ZipArchive(jarFile, Encoding.UTF8) {
  override def prefix = s"jar:$jarFile!/"
}

object JarArchive {
  def apply(f: Local.File) = new JarArchive(f)
  def apply(s: String) = new JarArchive(Local.File(s))
}
