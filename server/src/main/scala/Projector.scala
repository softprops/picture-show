package pictureshow

import unfiltered._
import unfiltered.request._
import unfiltered.response._
import java.net.URL

class Projector(path: URL) extends Renderer(path) with unfiltered.filter.Plan {
  def intent = {
    case Path("/") => HtmlContent ~>
      ResponseString(renderDefault)
  }
}
