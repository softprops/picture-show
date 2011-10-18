import sbt._
import Keys._

object build extends Build {
  
  import Deps._

  lazy val standardSettings = Defaults.defaultSettings ++ Seq(
    organization := "me.lessis",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.8.1",
    libraryDependencies += knockoff
  )

  lazy val root = Project(
    id = "PictureShow",
    base = file("."),
    aggregate = Seq(core, server, offln, conscript)
  )

  /** core transformations from txt files to html slide formated html */
  lazy val core = Project(
    id = "PictureShow Core",
    base = file("core"), 
    settings = standardSettings ++ Seq(
      libraryDependencies ++= Seq(codec, specs) 
    )
  )

  /** serves generated html on a configurable port */
  lazy val server = Project(
    id = "PictureShow Server",
    base = file("server"),  
    settings = standardSettings ++ Seq(
      libraryDependencies ++= Seq(uff, ufj)
    ),
    dependencies = Seq(core)  
  )


  /** caches generated html to disk */
  lazy val offln = Project(
    id = "PictureShow Offline",
    base = file("offline"),
    settings = standardSettings,
    dependencies = Seq(core)
  )

  /** command line client */
  lazy val conscript = Project(
    id = "PictureShow Conscript", 
    base = file("conscript"),
    settings = standardSettings ++ Seq(
      resolvers += Resolver.url("technically.us", new java.net.URL("http://databinder.net/repo/"))(Resolver.ivyStylePatterns),
      libraryDependencies += launch
    ),
    dependencies = Seq(core, server, offln)
  )



  object Deps {
    val knockoff = "net.databinder" %% "pamflet-knockoff" % "0.2.5"
    val codec = "commons-codec" % "commons-codec" % "1.4"
    val specs = "org.scala-tools.testing" %% "specs" % "1.6.8" % "test"
    val uf_version = "0.5.1"
    val uff = "net.databinder" %% "unfiltered-filter" % uf_version
    val ufj = "net.databinder" %% "unfiltered-jetty" % uf_version
    val launch = "org.scala-tools.sbt" % "launcher-interface" % "0.7.4" % "provided"
  }
}
