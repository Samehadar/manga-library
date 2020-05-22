package com.manga

import cats.effect.{ExitCode, IO, IOApp}
import cats.kernel.Monoid
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import cats.implicits._
import com.manga.manager.LibraryManager
import io.circe.syntax._
import org.http4s.circe.CirceEntityEncoder._
import com.manga.repository.InMemoryRepository
import io.circe.Printer

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    implicit val printer: Printer = Printer.spaces2.copy(dropNullValues = true)
    val library = new InMemoryRepository[IO]
    val manager = new LibraryManager[IO]

    val http = HttpRoutes.of[IO] {
      case GET -> Root / "hello" =>
        Ok("Hello, better world.")
    }

    val mangaListRoute = HttpRoutes.of[IO] {
      case GET -> Root / "list" =>
        val mangas = library.getList.map(_.asJson)
        Ok(mangas)
      case GET -> Root / "data" =>
        Ok(manager.get.map(_.asJson))
    }

    val httpApp = Router("/" -> http, "/manga" -> mangaListRoute).orNotFound


    BlazeServerBuilder[IO](global)
      .withHttpApp(httpApp)
      .bindHttp(8080, "localhost")
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)


  }
}
