package com.manga

import cats.effect.{Blocker, ContextShift, Sync}
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

case class MangaLibraryConfig(server: ServerConfig, database: DatabaseConfig)
case class ServerConfig(host: String, port: Int)
case class DatabaseConfig(port: Int, threadPoolSize: Int)

object MangaLibraryConfig {
  def load[F[_] : Sync : ContextShift](blocker: Blocker): F[MangaLibraryConfig] = {
    ConfigSource.default.at("manga-library").loadF[F, MangaLibraryConfig](blocker)
  }
}
