package pictureshow

import java.net.URI
import scala.util.parsing.json.JSON
import dispatch._

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
  private def gist = Http(:/("api.github.com").secure / "gists" / id as_str)
  private lazy val files: Map[String, String] = JSON.parseFull(gist).get match {
    case g: Map[String, Any] => g("files") match {
      case fs: Map[String, Any] => fs.map {
        case (k, meta: Map[String, String]) =>
          (k, meta("content"))
      }
    }
    case s => sys.error("unexpected response structure %s" format s)
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
  privte def lastSeg(p: String) = p.split(Pattern.quote(File.separator)).last
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
    val ID = """^/(.+)""".r
    def unapply(uri: URI) = (uri.getScheme, uri.getHost, uri.getPath) match {
      case ("https", "gist.github.com", ID(id)) => Some(id)
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
