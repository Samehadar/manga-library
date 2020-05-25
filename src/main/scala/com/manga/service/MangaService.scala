package com.manga.service

import cats.effect.Sync
import cats.implicits._
import com.manga.model.{Manga, MangaNotFoundError}
import com.manga.repository.MangaRepository
import fs2.Stream
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.headers.{Location, `Content-Type`}
import org.http4s.{HttpRoutes, MediaType, Uri}

class MangaService[F[_] : Sync](repository: MangaRepository[F]) extends Service[F] {

  override def routes: HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "list" =>
        //todo:: do it more pretty, now I am too sleepy
        Ok(Stream("[".asJson) ++ repository.get.map(_.asJson) ++ Stream("]".asJson), `Content-Type`(MediaType.application.json))

      case req @ POST -> Root / "add" =>
        for {
          manga <- req.as[Manga]
          createdManga <- repository.create(manga)
          response <- Created(createdManga.asJson, Location(Uri.unsafeFromString(s"/manga/${createdManga.id.get}")))
        } yield response

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