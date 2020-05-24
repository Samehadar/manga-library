package com.manga

import cats.effect.{Blocker, ContextShift, Sync}
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

case class MangaLibraryConfig(server: ServerConfig)
case class ServerConfig(host: String, port: Int)

object MangaLibraryConfig {
  def load[F[_] : Sync : ContextShift](blocker: Blocker): F[MangaLibraryConfig] = {
    ConfigSource.default.at("manga-library").loadF[F, MangaLibraryConfig](blocker)
  }
}
