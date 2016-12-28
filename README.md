## Poly-io 

[![Travis CI](https://img.shields.io/travis/USER/REPO.svg?style=flat-square)](https://travis-ci.org/ctongfei/poly-io)
[![Maven Central](https://img.shields.io/maven-central/v/me.tongfei/poly-io_2.12.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/me.tongfei/poly-io_2.12)
[![Scaladoc](https://img.shields.io/badge/javadoc.io-v0.3.2-ff69b4.svg?style=flat-square)](https://www.javadoc.io/doc/me.tongfei/poly-io_2.12)

`Poly-io` is a lightweight (no-dependencies), typesafe and easy-to-use library for file system I/O in Scala (2.11/2.12). 

```scala
    libraryDependencies += "me.tongfei" %% "poly-io" % "0.3.2"
```
### Features

##### Fluent idiomatic-Scala style file I/O
```scala
import poly.io.Local._

val lines = File("/usr/local/...").lines // type: Iterable[String]
val li = File("/home/foo/bar.gz") decompress Gzip decode Codec.ISOLatin1 linesIterator // type: Iterator[String]
```

##### Typesafe (dependent-type based) filesystem manipulation
```scala
val files = Local.Directory("/home/foo/a").recursiveChildren // type: Iterable[Local.Path]
val zipFile = files.find(_.name endsWith "zip").get
val z = ZipArchive(zipFile)
val filesInZip = z.root.recursiveChildren // type: Iterable[z.Path]
```

### Documentation
TODO
