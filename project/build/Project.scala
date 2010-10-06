import sbt._
class Project(info: ProjectInfo) extends DefaultProject(info) {
  val t_repo = "t_repo" at "http://tristanhunt.com:8081/content/groups/public/"
  val knockoff = "com.tristanhunt" %% "knockoff" % "0.7.0-10"
  
  val webroot = outputPath / "resources"
  
  val uf_vers = "0.2.1"
  val uff = "net.databinder" %% "unfiltered-filter" % uf_vers
  val ufj = "net.databinder" %% "unfiltered-jetty" % uf_vers
  
  // testing
  val snapshots = "Scala Tools Snapshots" at "http://www.scala-tools.org/repo-snapshots/"
  val specs = "org.scala-tools.testing" % "specs" % "1.6.2.2-SNAPSHOT" % "test"
}
