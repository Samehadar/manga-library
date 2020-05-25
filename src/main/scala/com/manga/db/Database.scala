package com.manga.db

import cats.effect.{Async, Blocker, ContextShift, Resource, Sync}
import com.manga.DatabaseConfig
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

object Database {


  def container[F[_] : Sync](config: DatabaseConfig): Resource[F, FixedPostgreSQLContainer] = Resource.liftF(
    Sync[F].delay {
      val container: FixedPostgreSQLContainer = new FixedPostgreSQLContainer()
      container.configurePort(config.port)
      container.start()
      container
    }
  )

  // Construct a transactor for connecting to the database.
  def transactor[F[_]: Async: ContextShift](
                                             container: FixedPostgreSQLContainer,
                                             ec: ExecutionContext,
                                             blocker: Blocker
                                           ): Resource[F, HikariTransactor[F]] =
  HikariTransactor.newHikariTransactor(
    container.getDriverClassName,
    container.getJdbcUrl,
    container.getUsername,
    container.getPassword,
    ec,
    blocker
  )

  def initFlyway[F[_] : Sync](transactor: HikariTransactor[F]): F[Unit] = {
    transactor.configure { dataSource =>
      Sync[F].delay {
        val flyWay = Flyway.configure().dataSource(dataSource).schemas("public").load()
        flyWay.migrate()
      }
    }
  }


}
