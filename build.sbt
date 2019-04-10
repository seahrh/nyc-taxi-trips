import sbt.Keys._

ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "2.12.7"
ThisBuild / organization := "com.sgcharts"

lazy val GatlingTest = config("gatling") extend Test

val versions = new {
  val gatling = "3.0.1.1"
}

// The Play project itself
lazy val root = (project in file("."))
  .enablePlugins(Common, PlayScala, GatlingPlugin)
  .configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
  .settings(
    name := """nyc-taxi-trips""",
    scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation",
    libraryDependencies ++= Seq(
      guice,
      "org.joda" % "joda-convert" % "2.1.2",
      "net.logstash.logback" % "logstash-logback-encoder" % "5.2",
      "com.netaporter" %% "scala-uri" % "0.4.16",
      "net.codingwell" %% "scala-guice" % "4.2.1",
      "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test,
      "io.gatling.highcharts" % "gatling-charts-highcharts" % versions.gatling % Test,
      "io.gatling" % "gatling-test-framework" % versions.gatling % Test
    ),
    maintainer := "seahrh@gmail.com"
  )

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/index.html
lazy val docs = (project in file("docs")).enablePlugins(ParadoxPlugin).
  settings(
    paradoxProperties += ("download_url" -> "https://example.lightbend.com/v1/download/play-rest-api")
  )
