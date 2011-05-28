package pictureshow

class Renderer(root: java.net.URL) extends Resolver(root)
  with Config with IO with Markup with Templates
  with Logging {
      
  def renderDefault = render(css(combineCss), mkSlides, js(combineJs))
}
