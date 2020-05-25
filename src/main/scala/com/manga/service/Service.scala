package com.manga.service

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

trait Service[F[_]] extends Http4sDsl[F] {
  def routes: HttpRoutes[F]
}
