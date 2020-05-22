name := "manga-library"

version := "0.1"

scalaVersion := "2.13.2"


val Http4sVersion = "0.21.4"
val testcontainersScalaVersion = "0.37.0"

//cats
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.3"

//http4s
libraryDependencies += "org.http4s" %% "http4s-core"          % Http4sVersion
libraryDependencies += "org.http4s" %% "http4s-dsl"           % Http4sVersion
libraryDependencies += "org.http4s" %% "http4s-blaze-server"  % Http4sVersion

//circe
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "io.circe" %% "circe-generic" % "0.12.2",
)

//db driver
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.12"

//logging
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2"
libraryDependencies += "ch.qos.logback"             %  "logback-classic" % "1.2.3"

//test containers
libraryDependencies += "org.scalatest"  %% "scalatest"                        % "3.1.2"                     % "test"
libraryDependencies += "com.dimafeng"   %  "testcontainers-scala_2.13"        % testcontainersScalaVersion  % "test"
libraryDependencies += "com.dimafeng"   %% "testcontainers-scala-scalatest"   % testcontainersScalaVersion  % "test"
libraryDependencies += "com.dimafeng"   %% "testcontainers-scala-postgresql"  % testcontainersScalaVersion  % "test"
