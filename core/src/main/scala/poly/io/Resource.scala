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

}

object Resource {

  def ofInputStream(f: => InputStream): Resource[InputStream] = new Resource[InputStream] {
    private[this] var is: InputStream = _
    def open() = { is = f; is }
    def close() = is.close()
  }

  def ofReader(f: => Reader): Resource[Reader] = new Resource[Reader] {
    private[this] var r: Reader = _
    def open() = { r = f; r }
    def close() = r.close()
  }

  def ofOutputStream(f: => OutputStream): Resource[OutputStream] = new Resource[OutputStream] {
    private[this] var os: OutputStream = _
    def open() = { os = f; os }
    def close() = os.close()
  }

  def ofWriter(f: => Writer): Resource[Writer] = new Resource[Writer] {
    private[this] var w: Writer = _
    def open() = { w = f; w }
    def close() = w.close()
  }

}