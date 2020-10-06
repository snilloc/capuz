// Play framework
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.2")
//addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.0")

// Scala formatting: "sbt scalafmt"
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.16")

// sbt-paradox, used for documentation
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.4.4")

//addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.11.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")
//addSbtPlugin("org.scoverage" %% "scalac-scoverage-runtime" % "1.4.1")

//addSbtPlugin("org.scalastyle" %% "scalastyle" % "1.0.0")

addSbtPlugin( "com.markatta" % "sbt-taglist" % "1.4.0")

// https://index.scala-lang.org/aiyanbo/sbt-dependency-updates/sbt-dependency-updates/1.2.2?target=_2.12_1.0
addSbtPlugin("org.jmotor.sbt" % "sbt-dependency-updates" % "1.2.2")

// scalafixSemanticdb
// https://scalacenter.github.io/scalafix/docs/users/installation.html
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.21")

// https://scalastyle.beautiful-scala.com/sbt.html
//addSbtPlugin("com.beautiful-scala" % "scalastyle" % "1.4.0")

// https://github.com/sbt/sbt-buildinfo#  BuildInfoPlugin
// addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")
//addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")

// Already in sbt 1.3.3 by default https://get-coursier.io/docs/sbt-coursier
// https://github.com/alexarchambault/coursier
// Faster artifact caching and resolution
///addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.1")

// Show a list of project dependencies that can be updated
// // https://github.com/johanandren/sbt-taglist 0.13 build only
//addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.1")


