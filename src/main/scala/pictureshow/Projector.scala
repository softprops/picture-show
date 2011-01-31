package pictureshow

import unfiltered._
import unfiltered.request._
import unfiltered.response._
import java.net.URL

class Projector(path: URL) extends Resolver(path) with Config with IO with Markup with Templates with Logging with unfiltered.filter.Plan {
  def intent = {
    case Path("/") => HtmlContent ~> ResponseString(render(css(combineCss), mkSlides, js(combineJs)).toString)
  }
}
