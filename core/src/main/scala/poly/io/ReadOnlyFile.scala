package poly.io


/**
 * @author Tongfei Chen
 * @since 0.3.0
 */
trait ReadOnlyFile[S <: ReadOnlyFileSystem] extends ReadOnlyPath[S] with TapeFile[S] { self: S#File =>

}
