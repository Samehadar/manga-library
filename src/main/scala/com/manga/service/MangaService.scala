package com.manga.service

import cats.effect.{ConcurrentEffect, Sync}
import com.manga.model.{Manga, MangaNotFoundError}
import com.manga.repository.MangaRepository
import io.circe.{Json, Printer}
import org.http4s.{HttpRoutes, Response, Status}
import org.http4s.dsl.io._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.CirceEntityDecoder._
import io.circe.syntax._
import org.http4s.dsl.Http4sDsl
import cats.implicits._

trait Service[F[_]] extends Http4sDsl[F] {
  def routes: HttpRoutes[F]
}

class MangaService[F[_] : Sync](repository: MangaRepository[F]) extends Service[F] {

  override def routes: HttpRoutes[F] = {
    HttpRoutes.of[F] {
//      case GET -> Root / "list" =>
//        Monad[F].pure(Response(Status.Ok))
//        Ok(Monad[F].map(repository.get)(x => x.asJson: Json))

//      case req @ POST -> Root / "add" =>
//        Monad[F].flatMap(req.as[Manga]){manga =>
//          manager.add(manga)
//          Ok(s"The manga has been added successfully: ${manga.title}")
//        }
      case GET -> Root / LongVar(id) =>
        repository.get(id).flatMap{
          case Left(MangaNotFoundError) => NotFound()
          case Right(manga) => Ok(manga.asJson)
        }

      case DELETE -> Root / LongVar(id) =>
        repository.delete(id).flatMap {
          case Left(MangaNotFoundError) => NotFound()
          case Right(_) => NoContent()
        }

    }
  }

}