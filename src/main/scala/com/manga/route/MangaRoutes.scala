package com.manga.route
import cats.effect.{ConcurrentEffect, Sync}
import cats.{Applicative, Defer, Monad, MonadError}
import com.manga.manager.LibraryManager
import com.manga.model.Manga
import io.circe.{Json, Printer}
import org.http4s.{HttpRoutes, Response, Status}
import org.http4s.dsl.io._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.CirceEntityDecoder._
import io.circe.syntax._
import org.http4s.dsl.Http4sDsl

class MangaRoutes[F[_] : Sync](manager: LibraryManager[F]) extends Route[F] {

  override def routes: HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "list" =>
//        Monad[F].pure(Response(Status.Ok))
        Ok(Monad[F].map(manager.get)(x => x.asJson: Json))

      case req @ POST -> Root / "add" =>
        Monad[F].flatMap(req.as[Manga]){manga =>
          manager.add(manga)
          Ok(s"The manga has been added successfully: ${manga.title}")
        }
      case GET -> Root / id =>
        Ok(Monad[F].map(manager.get(id.toLong))(x => x.asJson: Json))

      case DELETE -> Root / id =>
        Ok(Monad[F].map(manager.delete(id.toLong))(x => x.asJson: Json))

    }
  }

}