enablePlugins(JavaAppPackaging)

name := "manga-library"

version := "0.1"

scalaVersion := "2.13.2"
sbtVersion := "1.3.5"


//docker
maintainer in Docker := "Samehadar <stels95@list.ru>"
packageSummary in Docker := "A manga library"
packageDescription := "Docker Service written in Scala"
dockerExposedPorts := Seq(8080)


val doobieVersion = "0.8.8"
val Http4sVersion = "0.21.4"
val testcontainersScalaVersion = "0.37.0"
val PureConfigVersion = "0.12.3"

//cats
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.3"

//http4s
libraryDependencies += "org.http4s" %% "http4s-core"          % Http4sVersion
libraryDependencies += "org.http4s" %% "http4s-dsl"           % Http4sVersion
libraryDependencies += "org.http4s" %% "http4s-blaze-server"  % Http4sVersion

//pconfig
libraryDependencies += "com.github.pureconfig" %% "pureconfig"              % PureConfigVersion
libraryDependencies += "com.github.pureconfig" %% "pureconfig-cats-effect"  % PureConfigVersion

//circe
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "io.circe" %% "circe-generic" % "0.12.2",
)


//migrations
libraryDependencies += "org.flywaydb"   % "flyway-core" % "6.4.2"

//doobie
libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core"     % doobieVersion,
  "org.tpolecat" %% "doobie-hikari"   % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-specs2"   % doobieVersion % "test"
)

//logging
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2"
libraryDependencies += "ch.qos.logback"             %  "logback-classic" % "1.2.3"

//test containers
libraryDependencies += "org.scalatest"  %% "scalatest"                        % "3.1.2"                     % "test"
libraryDependencies += "com.dimafeng"   %% "testcontainers-scala"             % testcontainersScalaVersion  //% "test"
libraryDependencies += "com.dimafeng"   %% "testcontainers-scala-scalatest"   % testcontainersScalaVersion  % "test"
libraryDependencies += "com.dimafeng"   %% "testcontainers-scala-postgresql"  % testcontainersScalaVersion  //% "test"

libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-core" % "0.15.2"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % "0.15.2"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % "0.15.2"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s" % "0.15.3"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-redoc-http4s" % "0.15.3"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "0.15.3"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "0.15.3"