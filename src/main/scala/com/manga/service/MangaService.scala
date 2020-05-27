package com.manga.service

import cats.data.EitherT
import cats.effect.{ContextShift, Sync}
import cats.implicits._
import com.manga.model.{Manga, MangaNotFoundError}
import com.manga.repository.MangaRepository
import fs2.Stream
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.{HttpRoutes, Uri}
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._
import sttp.tapir.CodecFormat
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.CirceEntityDecoder._

class MangaService[F[_] : Sync : ContextShift](repository: MangaRepository[F]) extends Service[F] {

  private val allManga: Endpoint[Unit, StatusCode, Stream[F, Byte], Stream[F, Byte]] =
    endpoint.get
      .in("manga")
      .in("list")
      .errorOut(statusCode)
      .out(
        streamBody[Stream[F, Byte]](schemaFor[Byte], CodecFormat.Json())
//          .example(examples.toList.asJson.spaces2)
      )
      .description("Return all existing products in JSON format as a stream of bytes.")

  private val getManga = endpoint.get
    .in("manga" / path[Long]("id"))
    .out(jsonBody[Manga])
    .errorOut(statusCode)

  def endpoints = List(allManga, getManga)

  override def routes: HttpRoutes[F] = {
    val allMangaRoute: HttpRoutes[F] = allManga
      .toRoutes{_ =>
        val prefix = Stream.eval("[".pure[F])
        val suffix = Stream.eval("]".pure[F])
        val ps = repository.get
        .map(_.asJson.noSpaces).intersperse(",")
        val result: Stream[F, String]                     = prefix ++ ps ++ suffix
        val bytes: Stream[F, Byte]                        = result.through(fs2.text.utf8Encode)
        val response: Either[StatusCode, Stream[F, Byte]] = Right(bytes)
        response.pure[F]
      }

    val getMangaRoute: HttpRoutes[F] = getManga
        .toRoutes(id => EitherT(repository.get(id))
            .leftMap(_ => StatusCode.NotFound)
            .value
        )

    allMangaRoute <+> getMangaRoute
  }



//  override def routes: HttpRoutes[F] = {
//    HttpRoutes.of[F] {
//      case GET -> Root / "list" =>
//        //todo:: do it more pretty, now I am too sleepy
//        Ok(Stream("[".asJson) ++ repository.get.map(_.asJson) ++ Stream("]".asJson), `Content-Type`(MediaType.application.json))
//
//      case req @ POST -> Root / "add" =>
//        for {
//          manga <- req.as[Manga]
//          createdManga <- repository.create(manga)
//          response <- Created(createdManga.asJson, Location(Uri.unsafeFromString(s"/manga/${createdManga.id.get}")))
//        } yield response
//
//      case GET -> Root / LongVar(id) =>
//        repository.get(id).flatMap{
//          case Left(MangaNotFoundError) => NotFound()
//          case Right(manga) => Ok(manga.asJson)
//        }
//
//      case DELETE -> Root / LongVar(id) =>
//        repository.delete(id).flatMap {
//          case Left(MangaNotFoundError) => NotFound()
//          case Right(_) => NoContent()
//        }
//
//    }
//  }
}