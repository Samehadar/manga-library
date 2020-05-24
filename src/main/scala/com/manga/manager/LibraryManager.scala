package com.manga.manager
import java.time.LocalDate

import com.manga.model.{LibraryData, Manga}
import cats.Applicative
import cats.syntax.functor._
import com.manga.util.Time

class LibraryManager[F[_] : Applicative : Time] extends Manager[F, Manga] {

//  override def get: F[LibraryData] = Applicative[F].ap(Applicative[F].pure { localDate: LocalDate =>
//    val manga1 = Manga(1, "Wolf and Spices", localDate, 1)
//    val manga2 = Manga(2, "RWBY", LocalDate.of(2012, 1, 1), 1)
//    LibraryData(List(manga1, manga2))
//  })(Time[F].localDate)

  override def get: F[LibraryData] = Time[F].localDate.map{ localDate =>
    val manga1 = Manga(1, "Wolf and Spices", localDate, 1)
    val manga2 = Manga(2, "RWBY", LocalDate.of(2012, 1, 1), 1)
    LibraryData(List(manga1, manga2))
  }

  override def get(id: Long): F[Option[Manga]] = Applicative[F].pure {
    if (id > 10L)
      Option(Manga(id, "Wolf and Spices", LocalDate.of(2012, 1, 1), 1))
    else
      None
  }

  override def delete(id: Long): F[Boolean] =
    Applicative[F].pure(id > 10L)

  override def add(a: Manga): F[Manga] = Applicative[F].pure(a)

}

