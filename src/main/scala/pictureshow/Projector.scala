package pictureshow

import unfiltered._
import unfiltered.request._
import unfiltered.response._

class Projector(path: String) extends Resolver(path) with Config with IO with Markup with Templates with unfiltered.Plan {
  def filter = {
    case GET(Path("/", _)) => ResponseString(render(css(combineCss), mkSlides).toString)
  }
}