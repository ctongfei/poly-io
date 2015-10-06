package poly.io

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
trait Observer[-T] {
  def write(x: T)
  def close()
}
