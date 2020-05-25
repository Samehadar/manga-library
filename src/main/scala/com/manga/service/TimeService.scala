package com.manga.service

import cats.{Defer, Monad}
import com.manga.util.Time
import cats.implicits._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.HttpRoutes

class TimeService[F[_] : Defer : Monad : Time] extends Service[F] {
  override def routes: HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "time" =>
        Time[F].localDateTime.flatMap(time => Ok(time))
    }
  }
}