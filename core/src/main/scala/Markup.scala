package pictureshow

trait Markup { self: Resolver with Config =>
  import pamflet.PamfletDiscounter._

  /** combine all js assets */
  def combineJs = ("js/custom.js" :: Nil) filter exists
  /** combine all css assets */
  def combineCss = ("css/custom.css" :: Nil) filter exists
  /** loads and processes all markdown from configured sections */
  def mkSlides = {
    ((new xml.NodeBuffer, 0) /: sections) ((a, s) => {
      // FIXME gists can't have paths with depth
      val file = resolve("%s/%s.md" format (s, s))
      if(file.isEmpty) log("no file(s) at path %s/%s.md" format(s, s))
      val fileRes = (((new scala.xml.NodeBuffer, a._2) /: file)((m, f) => {
        val mdRes = parse(f, m._2)
        (m._1 &+ mdRes._1, mdRes._2)
      }))
      (a._1 &+ fileRes._1, fileRes._2)
    })._1
  }
  private def parse(content: String, index: Int) = {
    var Meta = """(?i)(HTML)""".r
    // (?m) enables multiline matching, so ^ anchors to start of line,
    // not start of input
    val slides = content.split("(?m)^!SLIDE")
    if(slides.isEmpty) log("no slides within file %s at index %s" format(content, index))
    ((new xml.NodeBuffer, index) /: slides.drop(1))( (a, s) => {
      (a._1 &+ (<div class="content" id={"slide-%s" format a._2}>{
        val lines = s.lines.toList
        lines.head.trim match {
          case Meta(meta) => {
            <div class="container">
              <div>{ xml.PCData( lines.drop(1).mkString("") ) }</div>
            </div>
          }
          case _ =>
            lines.partition(_.startsWith("#SUB ")) match {
              case (Nil, nonsubs) =>
                <div class="container">
                  {parseMarkdown(nonsubs.mkString("\n"))}
                </div>
              case (subs, nonsubs) =>
                <div class="container">
                  {parseMarkdown(nonsubs.mkString("\n"))}
                </div>
                <div class="subtitle">
                  {parseMarkdown(subs.map(_.drop(5)).mkString("\n"))}
                </div>
            }
        }}
      </div>), a._2 + 1)
    })
  }

  private def parseMarkdown(content: String) = toXHTML(knockoff(content))
}
