import sbt._
class Project(info: ProjectInfo) extends DefaultProject(info) {
  val knockoff = "com.tristanhunt" %% "knockoff" % "0.8.0-16"

  val webroot = outputPath / "resources"

  val uf_vers = "0.3.2"
  val uff = "net.databinder" %% "unfiltered-filter" % uf_vers
  val ufj = "net.databinder" %% "unfiltered-jetty" % uf_vers

  val launch = "org.scala-tools.sbt" % "launcher-interface" % "0.7.4" % "provided" // for conscript

  // testing
 // val snapshots = "Scala Tools Snapshots" at "http://www.scala-tools.org/repo-snapshots/"

  val specs = "org.scala-tools.testing" % "specs" % "1.6.2.2" % "test"
}
