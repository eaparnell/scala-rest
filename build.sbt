name := "scala-rest"

version := "0.1"

scalaVersion := "2.12.8"


libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.codehaus.groovy" % "groovy-all" % "2.5.4",
  "com.typesafe.akka" %% "akka-http" % "10.1.7",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7",
  "com.typesafe.akka" %% "akka-stream" % "2.5.20",
  "com.typesafe.akka" %% "akka-actor" % "2.5.20",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.20",
  "de.heikoseeberger" % "akka-http-circe_2.12" % "1.25.2"
)


val circeVersion = "0.10.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)