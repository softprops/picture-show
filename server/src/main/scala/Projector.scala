package pictureshow

import unfiltered._
import unfiltered.request._
import unfiltered.response._
import java.net.URI

class Projector(uri: URI) extends Renderer(uri) with unfiltered.filter.Plan {
  def intent = {
    case Path("/") => HtmlContent ~>
      ResponseString(renderDefault)
    case Path(p) =>
      if(exists(p)) resolve(p).map(ResponseString).getOrElse(Pass)
      else Pass
  }
}
