import sbt._
import Keys._

object Build extends sbt.Build {
  
  import Deps._

  lazy val standardSettings = Defaults.defaultSettings ++ Seq(
    organization := "me.lessis",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(knockoff, dispatch),
    scalacOptions += "-deprecation"
  )

  lazy val root = Project(
    "PictureShow",
    file("."),
    aggregate = Seq(core, server, offln, app)
  )

  /** core transformations from txt files to html slide formated html */
  lazy val core = Project(
    "PictureShow Core",
    file("core"),
    settings = standardSettings ++ Seq(
      libraryDependencies ++= Seq(codec, specs)
    )
  )

  /** serves generated html on a configurable port */
  lazy val server = Project(
    "PictureShow Server",
    file("server"),  
    settings = standardSettings ++ Seq(
      libraryDependencies ++= Seq(uff, ufj)
    ),
    dependencies = Seq(core)  
  )

  /** caches generated html to disk */
  lazy val offln = Project(
    "PictureShow Offline",
    file("offline"),
    settings = standardSettings,
    dependencies = Seq(core)
  )

  /** command line client, pshow */
  lazy val app = Project(
    "PictureShow Conscript", 
    file("conscript"),
    settings = standardSettings ++ conscript.Harness.conscriptSettings,
    dependencies = Seq(core, server, offln)
  )

  object Deps {
    val knockoff = "net.databinder" %% "pamflet-knockoff" % "0.3.1"
    val codec = "commons-codec" % "commons-codec" % "1.4"
    val specs = "org.scala-tools.testing" %% "specs" % "1.6.9" % "test"
    val uf_version = "0.5.3"
    val uff = "net.databinder" %% "unfiltered-filter" % uf_version
    val ufj = "net.databinder" %% "unfiltered-jetty" % uf_version
    val dispatch = "net.databinder" %% "dispatch-http" % "0.8.7"
  }
}
