
name := """capuz"""
organization := "com.example"
version := "1.0-SNAPSHOT"
scalaVersion := "2.13.3"
sbtVersion := "1.3.13"

lazy val root = (project in file(".")).enablePlugins(PlayScala).
  enablePlugins(BuildInfoPlugin).
  settings(
    addCompilerPlugin(scalafixSemanticdb),
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.yp.capuz.version",
    buildInfoOptions += BuildInfoOption.ToMap,
    buildInfoOptions += BuildInfoOption.ToJson
  )


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "org.kbatz.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "org.kbatz.binders._"
//javacOptions  ++= Seq("-source", "11.0.8", "-target", "11.0.8")


libraryDependencies ++= Seq(
  jdbc,
  guice,
  ws,
  specs2 % Test,
  //compilerPlugin("com.github.ghik" %% "silencer-plugin" % "1.4.2"),
  "org.mockito" % "mockito-core" % "3.5.10" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  "commons-io" % "commons-io" % "2.8.0",
  "org.scalacheck" %% "scalacheck" % "1.14.1" % Test,
//  "com.github.cb372" %% "scalacache-caffeine" % "0.28.0",  // replace play's cache with scalacache
//  "org.pac4j" % "play-pac4j" % "2.3.2",
//  "org.pac4j" % "pac4j-cas" % "1.9.9" withSources() withJavadoc(),
//  "org.pac4j" % "pac4j-oauth" % "1.9.9" withSources() withJavadoc(),

//  "org.lwes" % "lwes-java" % "2.2.1",
//  "org.squeryl" % "squeryl_2.11" % "0.9.15" withSources() withJavadoc(),
//  "com.zaxxer" % "HikariCP" % "3.4.5", // the version that play uses is too old
//  "com.maginatics" % "jdbclint" % "0.5.0", // detect bad coding around jdbc connection handling
//  "org.apache.commons" % "commons-csv" % "1.8",
//  "org.apache.poi" % "poi" % "4.1.2",
//  "org.apache.poi" % "poi-ooxml" % "4.1.2",
//  "de.siegmar" % "logback-gelf" % "2.1.2",
//  "org.scala-graph" %% "graph-core" % "1.12.5",
//  "org.scala-graph" %% "graph-dot" % "1.11.5",
//   "com.github.ghik" %% "silencer-lib" % "1.4.2" % Provided
)

// unmanagedResourceDirectories in Compile ++= Seq(baseDirectory.value / "resources")
//scalaSource in Test ++= Seq(baseDirectory.value / "test")

logBuffered in Test := false
parallelExecution := true
fork in Test := false
parallelExecution in Test := true
routesGenerator := InjectedRoutesGenerator

// END of Play configuration

addCommandAlias("devbuild", ";" +
  List(
    "dependencyUpdates", // addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.1")
    "tagList",         // addSbtPlugin( "com.markatta" % "sbt-taglist" % "1.4.0")
    "clean",           // deletes all compiled arttifacts and forces slow/full compile
    "coverage",        // 2s - do this BEFORE compile and before scalafix to only compile 1 time
    "scalastyle",      // 19s - no compilation required
    "test:scalastyle", // 19s - no compilation required
    "doc",             // 40s - no compilation required but sbt clean wipes it out
    "openDoc",         // open in browser - you should review it while compile runs
    "buildInfo",       // force buildInfo to be generated once with coverage enabled

    // below here is compilation dependent
    // scalafixCheck forces recompilation AND rebuild of BuildInfo which then gets incorrectly used as only covered code
    "scalafixCheck",   // triggers compile - formats app, compiles app, then formats test, compiles test
    // redundant with scalafixCheck "test:compile",    // 115s formats and does full compile -- no other steps should recompile
    "test",            // No compile should happen here if coverage task occurs before all other compile tasks
    "coverageReport",
    "openCoverageReport").mkString(";"))

// BEGIN sbt-scoverage
lazy val openCoverageReport = taskKey[Unit]("Open Coverage Report")


openCoverageReport := {
  // sbt 0.13.13+ "open target/scala-2.11/sbt-0.13/scoverage-report/index.html" !
  // "open target/scoverage-report/index.html" !
}

// coverageExcludedPackages := "<empty>;.*BuildInfo.*;script.*;clients.script_extensions.*;com.yp.scratch.*;swagger.*;com\\.yp\\.gump\\.version;"
//coverageExcludedPackages := "<empty>;.*BuildInfo.*;script.*;clients.script_extensions.*;com.yp.scratch.*;com\\.yp\\.gump\\.version;"
coverageExcludedPackages := "<empty>;Reverse.*;.*AuthService.*;models\\.data\\..*"
coverageMinimum := 36
coverageFailOnMinimum := true
coverageHighlighting := true
coverageEnabled := true

// BEGIN Test Options
// https://github.com/sbt/sbt/issues/1485
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")  // add durations

sources in tagList := (sources in Compile).value ++ (sources in Test).value

tagListTags := Set(
  TagListPlugin.Tag("BROKEN", TagListPlugin.Error),
  TagListPlugin.Tag("REFACTOR", TagListPlugin.Warn),
  TagListPlugin.Tag("FIXME", TagListPlugin.Warn),
  TagListPlugin.Tag("TODO", TagListPlugin.Info)
)