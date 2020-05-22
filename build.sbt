name := "manga-library"

version := "0.1"

scalaVersion := "2.13.2"


val Http4sVersion = "0.21.4"

libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.3"
libraryDependencies += "org.http4s" %% "http4s-core" % Http4sVersion
libraryDependencies += "org.http4s" %% "http4s-dsl"  % Http4sVersion
libraryDependencies += "org.http4s" %% "http4s-blaze-server" % Http4sVersion
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  // Optional for auto-derivation of JSON codecs
  "io.circe" %% "circe-generic" % "0.12.2",
)