
// https://github.com/sbt/sbt-buildinfo#  BuildInfoPlugin
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")

// Already in sbt 1.3.3 by default https://get-coursier.io/docs/sbt-coursier
// https://github.com/alexarchambault/coursier
// Faster artifact caching and resolution
//addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.1")


addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.2")
//addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.11.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

addSbtPlugin( "com.markatta" % "sbt-taglist" % "1.4.0")

// Show a list of project dependencies that can be updated
// // https://github.com/johanandren/sbt-taglist
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.1")


// scalafixSemanticdb
// https://scalacenter.github.io/scalafix/docs/users/installation.html
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.20")