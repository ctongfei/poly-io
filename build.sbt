import sbt.Keys._

lazy val commonSettings = Seq(
  organization := "me.tongfei",
  scalaVersion := "2.11.8",
  version := "0.4.0-SNAPSHOT",
  isSnapshot := true,
  crossScalaVersions := Seq("2.11.8", "2.12.1"),

  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomExtra :=
    <url>http://github.com/ctongfei/poly-io</url>
      <licenses>
        <license>
          <name>MIT</name>
          <url>http://opensource.org/licenses/MIT</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:ctongfei/poly-io.git</url>
        <connection>scm:git:git@github.com:ctongfei/poly-io.git</connection>
      </scm>
      <developers>
        <developer>
          <id>ctongfei</id>
          <name>Tongfei Chen</name>
          <url>http://tongfei.me/</url>
        </developer>
      </developers>
)

lazy val core =
  (project in file("core")).settings(commonSettings: _*).settings(
    name := "poly-io-core"
  )

lazy val archive =
  (project in file("archive")).settings(commonSettings: _*)
    .dependsOn(core)
    .settings(
      name := "poly-io-archive",
      libraryDependencies += "org.apache.commons" % "commons-compress" % "1.12"
    )
