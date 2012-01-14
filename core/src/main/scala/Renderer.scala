package pictureshow

class Renderer(uri: java.net.URI) extends Resolved(uri)
  with Config
  with Markup
  with Templates
  with Logging {
      
  def renderDefault = render(css(combineCss), mkSlides, js(combineJs))
}
