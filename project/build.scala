import sbt._
import Keys._

object Build extends sbt.Build {
  
  import Deps._

  lazy val standardSettings = Defaults.defaultSettings ++ Seq(
    organization := "me.lessis",
    version := "0.1.1",
    scalaVersion := "2.9.2", //  knockoff not published for anything greater
    libraryDependencies ++= Seq(knockoff, dispatch),
    scalacOptions ++= Seq("-deprecation", "-unchecked")
  ) ++ Seq(
    homepage := Some(url("https://github.com/softprops/picture-show")),
    publishMavenStyle := true,
    publishTo := Some(Opts.resolver.sonatypeStaging),
    publishArtifact in Test := false,
    licenses <<= (version)(v =>
      Seq("MIT" -> url("https://github.com/softprops/picture-show/%s/LICENSE".format(v)))
    ),
    pomExtra := (
      <scm>
        <url>git@github.com:softprops/picture-show.git</url>
        <connection>scm:git:git@github.com:softprops/picture-show.git</connection>
      </scm>
      <developers>
        <developer>
          <id>softprops</id>
          <name>Doug Tangren</name>
          <url>https://github.com/softprops</url>
        </developer>
      </developers>)
  )

  lazy val root = Project(
    "pictureshow-all",
    file("."),
    settings = standardSettings ++ Seq(
      test := { },
      publish := { }, // skip publishing for this root project.
      publishLocal := { }
    ),
    aggregate = Seq(core, server, offln, app)
  )

  /** core transformations from txt files to html slide formated html */
  lazy val core = Project(
    "pictureshow-core",
    file("core"),
    settings = standardSettings ++ Seq(
      libraryDependencies ++= Seq(codec, specs)
    )
  )

  /** serves generated html on a configurable port */
  lazy val server = Project(
    "pictureshow-server",
    file("server"),  
    settings = standardSettings ++ Seq(
      libraryDependencies ++= Seq(uff, ufj)
    ),
    dependencies = Seq(core)  
  )

  /** caches generated html to disk */
  lazy val offln = Project(
    "pictureshow-offline",
    file("offline"),
    settings = standardSettings,
    dependencies = Seq(core)
  )

  /** command line client, pshow */
  lazy val app = Project(
    "pictureshow-conscript", 
    file("conscript"),
    settings = standardSettings ++ Seq(
      libraryDependencies <+= (sbtVersion) { v =>
        "org.scala-sbt" % "launcher-interface" % v % "provided"
      }
    ),
    dependencies = Seq(core, server, offln)
  )

  object Deps {
    val knockoff = "net.databinder" %% "pamflet-knockoff" % "0.4.2"
    val codec = "commons-codec" % "commons-codec" % "1.4"
    val specs = "org.scala-tools.testing" %% "specs" % "1.6.9" % "test"
    val uf_version = "0.6.1"
    val uff = "net.databinder" %% "unfiltered-filter" % uf_version
    val ufj = "net.databinder" %% "unfiltered-jetty" % uf_version
    val dispatch = "net.databinder.dispatch" %% "json4s-native" % "0.9.4"
  }
}
