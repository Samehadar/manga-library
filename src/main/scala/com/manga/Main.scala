package com.manga

import cats.Applicative
import cats.effect._
import org.http4s.server.{Router, Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import cats.implicits._
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.manga.manager.LibraryManager
import com.manga.route.MangaRoutes
import org.flywaydb.core.Flyway
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.HttpRoutes

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {

  // Construct a transactor for connecting to the database.
  def transactor[F[_]: Async: ContextShift](
                                             container: PostgreSQLContainer,
                                             ec: ExecutionContext,
                                             blocker: Blocker
                                           ): Resource[F, HikariTransactor[F]] =
      HikariTransactor.newHikariTransactor(
        container.driverClassName,
        container.jdbcUrl,
        container.username,
        container.password,
        ec,
        blocker
      )

  private def container[F[_] : Applicative]: Resource[F, PostgreSQLContainer] = Resource.liftF(
    Applicative[F].pure {
      val container = PostgreSQLContainer()
      container.start()
      container
    }
  )

  def initFlyway[F[_] : Sync](transactor: HikariTransactor[F]): F[Unit] = {
    transactor.configure { dataSource =>
      Sync[F].delay {
        val flyWay = Flyway.configure().dataSource(dataSource).schemas("flyway").load()
        flyWay.migrate()
      }
    }
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
      container   <- container[F]
      ec          <- ExecutionContexts.fixedThreadPool[F](10)//todo:: get this from config
      transactor  <- transactor[F](container, ec, blocker)
      _           <- Resource.liftF(initFlyway(transactor))
      manager     =  new LibraryManager[F] //todo:: put transactor into
      httpApp     =  Router("manga" -> new MangaRoutes[F](manager).routes)
      server      <- server[F](config.server, httpApp)
    } yield Resources[F](server, transactor, config)
  }


  override def run(args: List[String]): IO[ExitCode] = {
    resources[IO].use(_ => IO.never).as(ExitCode.Success)
  }

  case class Resources[F[_]](server: Server[F], transactor: HikariTransactor[F], config: MangaLibraryConfig)
}
