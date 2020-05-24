package com.manga

import java.sql.DriverManager

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import cats.implicits._
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.manga.manager.LibraryManager
import com.manga.repository.InMemoryRepository
import com.manga.route.MangaRoutes
import io.circe.Printer

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val container = PostgreSQLContainer()
    container.start()

    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
    connection.createStatement().executeQuery(container.testQueryString)


    implicit val printer: Printer = Printer.spaces2.copy(dropNullValues = true)
    val library = new InMemoryRepository[IO]
    val manager = new LibraryManager[IO]



    val mangaRoutes = new MangaRoutes[IO](manager).routes

    val httpApp = Router("manga" -> mangaRoutes).orNotFound


    BlazeServerBuilder[IO](global)
      .withHttpApp(httpApp)
      .bindHttp(8080, "0.0.0.0")
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)


  }
}
