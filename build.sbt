import sbt.Keys._

ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "2.12.8"
ThisBuild / organization := "com.sgcharts"

lazy val GatlingTest = config("gatling") extend Test

val versions = new {
  val gatling = "3.0.3"
}

// The Play project itself
lazy val root = (project in file("."))
  .enablePlugins(Common, PlayScala, GatlingPlugin)
  .configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
  .settings(PlayKeys.playDefaultPort := 8080)
  .settings(
    name := """nyc-taxi-trips""",
    scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation",
    libraryDependencies ++= Seq(
      guice,
      "org.joda" % "joda-convert" % "2.2.0",
      "net.logstash.logback" % "logstash-logback-encoder" % "5.3",
      "com.netaporter" %% "scala-uri" % "0.4.16",
      "net.codingwell" %% "scala-guice" % "4.2.3",
      "io.sgr" % "s2-geometry-library-java" % "1.0.0",
      "com.google.cloud" % "google-cloud-bigquery" % "1.69.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test,
      "io.gatling.highcharts" % "gatling-charts-highcharts" % versions.gatling % Test,
      "io.gatling" % "gatling-test-framework" % versions.gatling % Test,
      "org.mockito" % "mockito-core" % "2.27.0" % Test
    ),
    maintainer := "seahrh@gmail.com"
  )

wartremoverErrors ++= Warts.allBut(
  Wart.ToString,
  Wart.Throw,
  Wart.DefaultArguments,
  Wart.ImplicitParameter,
  Wart.NonUnitStatements,
  Wart.Var,
  Wart.Overloading,
  Wart.MutableDataStructures,
  Wart.Equals,
  Wart.AsInstanceOf, // used in routes
  Wart.Nothing, // used in routes
  Wart.Product, // used in routes
  Wart.Serializable // used in routes
)

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/index.html
lazy val docs = (project in file("docs")).enablePlugins(ParadoxPlugin).
  settings(
    paradoxProperties += ("download_url" -> "https://example.lightbend.com/v1/download/play-rest-api")
  )
