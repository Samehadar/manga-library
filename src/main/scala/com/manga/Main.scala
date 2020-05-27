package com.manga

import cats.effect._
import org.http4s.server.{Router, Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import cats.implicits._
import com.manga.db.Database
import com.manga.repository.MangaRepository
import com.manga.service.{MangaService, TimeService}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.HttpRoutes
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {
  case class Resources[F[_]](server: Server[F], transactor: HikariTransactor[F], config: MangaLibraryConfig)

  override def run(args: List[String]): IO[ExitCode] = {
    resources[IO].use(_ => IO.never).as(ExitCode.Success)
  }

  // Resource that mounts the given `routes` and starts a server.
  private def server[F[_]: ConcurrentEffect: ContextShift: Timer](
                                                           config: ServerConfig,
                                                           routes: HttpRoutes[F]
                                                         ): Resource[F, Server[F]] =
    BlazeServerBuilder[F](global)
      .withHttpApp(routes.orNotFound)
      .bindHttp(config.port, config.host)
      .resource

  //todo:: move out ConcurrentEffect as far as I can
  private def resources[F[_] : ConcurrentEffect : Timer : ContextShift]: Resource[F, Resources[F]] = {
    for {
      blocker     <- Blocker[F]
      config      <- Resource.liftF(MangaLibraryConfig.load[F](blocker))
      container   <- Database.container[F](config.database)
      ec          <- ExecutionContexts.fixedThreadPool[F](config.database.threadPoolSize)
      transactor  <- Database.transactor[F](container, ec, blocker)
      _           <- Resource.liftF(Database.initFlyway[F](transactor))
      repo        =  MangaRepository.fromTransactor[F](transactor)
      mangsS      =  new MangaService[F](repo)
      httpApp     =  Router("/" -> (mangsS.routes <+> openAPI(mangsS)))//, "/" -> new TimeService[F].routes)
      server      <- server[F](config.server, httpApp)
    } yield Resources[F](server, transactor, config)
  }

  private def openAPI[F[_] : ContextShift : Sync](mangaService: MangaService[F]): HttpRoutes[F] = {
    val openApiDocs: OpenAPI = mangaService.endpoints.toOpenAPI("The tapir library", "1.0.0")
    val openApiYml: String = openApiDocs.toYaml

    new SwaggerHttp4s(openApiYml).routes[F]
  }

}
