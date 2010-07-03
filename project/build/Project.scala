import sbt._
class Project(info: ProjectInfo) extends DefaultProject(info) {
  val t_repo = "t_repo" at "http://tristanhunt.com:8081/content/groups/public/"
  val knockoff = "com.tristanhunt" %% "knockoff" % "0.7.0-10"
  
  val webroot = outputPath / "resources"
  
  val uf = "net.databinder" %% "unfiltered" % "0.1.2"
  val ufs = "net.databinder" %% "unfiltered-server" % "0.1.2"
}