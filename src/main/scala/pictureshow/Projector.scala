package pictureshow

import unfiltered._
import unfiltered.request._
import unfiltered.response._
import java.net.URL

class Projector(path: URL) extends Resolver(path) with Config with IO with Markup with Templates with Logging with unfiltered.Plan {
  def filter = {
    case GET(Path("/", _)) => ResponseString(render(css(combineCss), mkSlides).toString)
  }
}