package com.manga.db

import cats.effect.{Async, Blocker, ContextShift, Resource, Sync}
import com.dimafeng.testcontainers.PostgreSQLContainer
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

object Database {


  def container[F[_] : Sync]: Resource[F, PostgreSQLContainer] = Resource.liftF(
    Sync[F].delay {
      val container = PostgreSQLContainer()
      container.start()
      container
    }
  )

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

  def initFlyway[F[_] : Sync](transactor: HikariTransactor[F]): F[Unit] = {
    transactor.configure { dataSource =>
      Sync[F].delay {
        val flyWay = Flyway.configure().dataSource(dataSource).schemas("public").load()
        flyWay.migrate()
      }
    }
  }


}
