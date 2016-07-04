package poly

package object io {

  type Closeable = java.io.Closeable

  type InputStream = java.io.InputStream

  type OutputStream = java.io.OutputStream

  type PosixFilePermission = java.nio.file.attribute.PosixFilePermission

  object PosixFilePermission {
    import java.nio.file.attribute.PosixFilePermission._
    val OwnerRead = OWNER_READ
    val OwnerWrite = OWNER_WRITE
    val OwnerExecute = OWNER_EXECUTE
    val GroupRead = GROUP_READ
    val GroupWrite = GROUP_WRITE
    val GroupExecute = GROUP_EXECUTE
    val OthersRead = OTHERS_READ
    val OthersWrite = OTHERS_WRITE
    val OthersExecute = OTHERS_EXECUTE
  }

}
