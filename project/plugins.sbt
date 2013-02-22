resolvers += Resolver.url("sbt-plugin-releases", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

addSbtPlugin("net.databinder" % "conscript-plugin" % "0.3.5")

addSbtPlugin("com.jsuereth" % "xsbt-gpg-plugin" % "0.6")
