import sbt._

class PictureShow(info: ProjectInfo) extends ParentProject(info) with posterous.Publish {
  class PictureShowModule(info: ProjectInfo) extends DefaultProject(info) {
    val knockoff = "com.tristanhunt" %% "knockoff" % "0.8.0-16"
  }

  /** core transformations from txt files to html slide formated html */
  lazy val core = project("core", "PictureShow", new PictureShowModule(_) {
     val specs = "org.scala-tools.testing" % "specs" % "1.6.2.2" % "test"
  })

  /** serves generated html on a configurable port */
  lazy val server = project("server", "PictureShow Server", new PictureShowModule(_) {
    val uf_version = "0.3.3"
    val uff = "net.databinder" %% "unfiltered-filter" % uf_version
    val ufj = "net.databinder" %% "unfiltered-jetty" % uf_version
    val webroot = outputPath / "resources" // may not need this?
  }, core)

  /** caches generated html to disk */
  lazy val offln = project("offline", "PictureShow Offline", new PictureShowModule(_), core)

  /** command line client */
  lazy val conscript = project("conscript", "PictureShow Conscript", new PictureShowModule(_) {
     val launch = "org.scala-tools.sbt" % "launcher-interface" % "0.7.4" % "provided"
  }, core, server, offln)
}
