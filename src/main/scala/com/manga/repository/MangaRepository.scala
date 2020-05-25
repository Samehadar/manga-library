package com.manga.repository

import cats.effect.Sync
import com.manga.model.{Manga, MangaNotFoundError}
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.implicits.legacy.localdate._
import cats.syntax.functor._
import fs2.Stream

trait MangaRepository[F[_]] {
  def get: Stream[F, Manga]
  def get(id: Long): F[Either[MangaNotFoundError.type, Manga]]
  def delete(id: Long): F[Either[MangaNotFoundError.type, Unit]]
  def create(manga: Manga): F[Manga]
}


object MangaRepository {
  def fromTransactor[F[_] : Sync](transactor: Transactor[F]): MangaRepository[F] =
    new MangaRepository[F] {

      override def get(id: Long): F[Either[MangaNotFoundError.type, Manga]] =
        (sql"SELECT id, title, release_date, author_id FROM manga WHERE id = $id")
          .query[Manga].option.transact(transactor).map {
          case Some(manga) => Right(manga)
          case None => Left(MangaNotFoundError)
        }

      override def delete(id: Long): F[Either[MangaNotFoundError.type, Unit]] =
        (sql"DELETE FROM manga WHERE id = $id")
          .update.run.transact(transactor).map { affectedRows =>
          if (affectedRows == 1) {
            Right(())
          } else {
            Left(MangaNotFoundError)
          }
        }

      override def get: Stream[F, Manga] =
        sql"SELECT id, title, release_date, author_id FROM manga"
          .query[Manga].stream.transact(transactor)

      override def create(manga: Manga): F[Manga] =
        sql"INSERT INTO manga(title, release_date, author_id) values (${manga.title}, ${manga.releaseDate}, ${manga.authorId})"
        .update.withUniqueGeneratedKeys[Long]("id").transact(transactor).map { id =>
          manga.copy(id = Some(id))
        }
    }




}
