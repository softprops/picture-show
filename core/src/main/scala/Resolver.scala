package pictureshow

import java.net.URI
import dispatch._
import org.json4s._
import org.json4s.native.JsonMethods._

trait Resolver {
  def configuration: String
  def resolve(p: String): Option[String]
  def exists(p: String): Boolean
}

class GistGitResolver(uri: URI) extends Resolver {
  lazy val configuration = "todo"
  def resolve(p: String) = Some("todo")
  def exists(p: String) = false
}

class GistHttpResolver(id: String) extends Resolver {
  def gh = :/("api.github.com").secure <:< Map("User-Agent"->"picture-show/0.1.0") 
  private lazy val files: Map[String, String] = {
    (for {
      JObject(fs) <- Http(gh / "gists" / id > as.json4s.Json)()
      ("files", JObject(files)) <- fs
      (name, JObject(data)) <- files
      ("content", JString(content)) <- data
    } yield (name, content)).toMap
  }

  lazy val configuration = files("conf.js")
  def lastSeg(p: String) = p.split('/').last
  def resolve(p: String) = files.get(lastSeg(p))
  def exists(p: String)  = files isDefinedAt lastSeg(p)
}

class FileSystemResolver(uri: URI) extends Resolver {
  import java.io.File
  import java.net.URL
  import java.util.regex.Pattern
  private def file(p: String) = new File(uri.toURL.getFile, p)
  private def url(p: String) = new URL(uri.toURL, p)
  private def lastSeg(p: String) = p.split(Pattern.quote(File.separator)).last
  def configuration = IO.slurp(url("conf.js")).get
  def resolve(p: String) =
    if(file(p) exists) IO.slurp(url(p))
    else IO.slurp(
      url(lastSeg(p))
    )
  def exists(p: String) = file(p).exists || file(lastSeg(p)).exists
}

// resolves local (filesystem) & remote (git/gist) shows
class Resolved(uri: URI) extends Resolver {

  object GistHttp {
    val ID = """^(.+)/(.+)""".r
    def unapply(uri: URI) = (uri.getScheme, uri.getHost, uri.getPath) match {
      case ("https", "gist.github.com", ID(_, id)) => Some(id)
      case _ => None
    }
  }

  object GistGit {
    def unapply(uri: URI) = (uri.getScheme, uri.getHost) match {
      case ("git", "gist.github.com") => Some(uri)
      case _ => None
    }
  }
  
  object FileSystem {
    def unapply(uri: URI) = uri.getScheme match {
      case "file" => Some(uri)
      case _ => None
    }
  }

  private lazy val adapted = uri match {
    case GistHttp(uri) => new GistHttpResolver(uri)
    case GistGit(uri) => new GistGitResolver(uri)
    case FileSystem(uri) => new FileSystemResolver(uri)
    case uh => sys.error("can not adapt to resolving show from uri %s" format uh)
  }
  
  def configuration = adapted.configuration
  def resolve(p: String) = adapted.resolve(p)
  def exists(p: String) = adapted.exists(p)

}
