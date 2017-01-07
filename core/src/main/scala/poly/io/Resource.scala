package poly.io

/**
 * Encapsulates an object holding some resource in a monad.
 * @author Tongfei Chen
 * @since 0.4.0
 */
trait Resource[+R] { self =>

  /** Opens this resource. */
  def open(): R

  /** Closes this resource. */
  def close()

  /**
   * Performs an operation on the resource, and close it after use.
   * Normally used with `for` clause:
   * @example {{{
   *   for (r <- someResource) {
   *     ...
   *   }
   * }}}
   * The resource will automatically be closed after the end of the `for` clause.
   */
  def foreach[U](f: R => U): Unit = {
    val r = open()
    try f(r) finally close()
  }

  /** Transforms this resource by a function. */
  def map[S](f: R => S): Resource[S] = new Resource.Mapped(self, f)

  /**
   * Composes two resources, one being dependent on the other.
   * Normally used with `for` clause:
   * @example {{{ for (r1 <- resource1; r2 <- resource2(r1)) {...} }}}
   */
  def flatMap[S](f: R => Resource[S]): Resource[S] = new Resource.FlatMapped(self, f)

  def product[S](that: Resource[S]) = for (r <- this; s <- that) yield (r, s)

  def productWith[S, T](that: Resource[S])(f: (R, S) => T) = for (r <- this; s <- that) yield f(r, s)

}

object Resource {

  /**
   * Encapsulates a [[AutoCloseable]] resource in a [[Resource]] monad.
   */
  def apply[R <: AutoCloseable](f: => R): Resource[R] = new Resource[R] {
    private[this] var resource: R = _
    def open() = { resource = f; resource }
    def close() = resource.close()
  }

  private class Mapped[R, S](self: Resource[R], f: R => S) extends Resource[S] {
    def open() = f(self.open())
    def close() = self.close()
  }

  private class FlatMapped[R, S](self: Resource[R], f: R => Resource[S]) extends Resource[S] {
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

}
