package poly.io

/**
 * Encapsulates an object holding some resource in a monad.
 * @author Tongfei Chen
 * @since 0.4.0
 */
trait Resource[+R] { self =>

  def open(): R
  def close()

  def foreach[U](f: R => U) = {
    val r = open()
    f(r)
    close()
  }

  def map[S](f: R => S): Resource[S] = new Resource[S] {
    def open() = f(self.open())
    def close() = self.close()
  }

  def flatMap[S](f: R => Resource[S]): Resource[S] = new Resource[S] {
    private[this] var rs: Resource[S] = _
    def open() = {
      rs = f(self.open())
      rs.open()
    }
    def close() = {
      rs.close()
      self.close()
    }
  }

  def product[S](that: Resource[S]) = for (r <- this; s <- that) yield (r, s)

  def productWith[S, T](that: Resource[S])(f: (R, S) => T) = for (r <- this; s <- that) yield f(r, s)

}

object Resource {

  def ofCloseable[R <: AutoCloseable](f: => R): Resource[R] = new Resource[R] {
    private[this] var resource: R = _
    def open = { resource = f; resource }
    def close() = resource.close()
  }

}