package poly.io

import java.nio.file.{ Files => JFiles, Path => JPath }

import scala.collection._
import scala.collection.JavaConversions._

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
abstract class BaseFile {

  def j: JPath
  def fullName: String



  //region Filesystem attributes
  def isDirectory = JFiles.isDirectory(j)
  def isRegularFile = JFiles.isRegularFile(j)
  def isSymbolicLink = JFiles.isSymbolicLink(j)
  def isHidden = JFiles.isHidden(j)
  def isExecutable = JFiles.isExecutable(j)
  def isReadable = JFiles.isReadable(j)
  def isWritable = JFiles.isWritable(j)
  
  //region POSIX permissions
  def permissions: mutable.Set[PosixFilePermission] = new mutable.Set[PosixFilePermission] {
    def +=(elem: PosixFilePermission) = { JFiles.setPosixFilePermissions(j, this + elem); this }
    def -=(elem: PosixFilePermission) = { JFiles.setPosixFilePermissions(j, this - elem); this }
    def contains(elem: PosixFilePermission) = JFiles.getPosixFilePermissions(j).contains(elem)
    def iterator = JFiles.getPosixFilePermissions(j).iterator()
  }
  //endregion


}
