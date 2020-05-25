package com.manga

import cats.effect._
import org.http4s.server.{Router, Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import cats.implicits._
import com.manga.db.Database
import com.manga.manager.LibraryManager
import com.manga.route.MangaRoutes
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.HttpRoutes

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {
  case class Resources[F[_]](server: Server[F], transactor: HikariTransactor[F], config: MangaLibraryConfig)

  override def run(args: List[String]): IO[ExitCode] = {
    resources[IO].use(_ => IO.never).as(ExitCode.Success)
  }

  // Resource that mounts the given `routes` and starts a server.
  def server[F[_]: ConcurrentEffect: ContextShift: Timer](
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
      manager     =  new LibraryManager[F] //todo:: put transactor into
      httpApp     =  Router("manga" -> new MangaRoutes[F](manager).routes)
      server      <- server[F](config.server, httpApp)
    } yield Resources[F](server, transactor, config)
  }

}
