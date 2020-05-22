package com.manga.repository

import cats.Applicative

class InMemoryRepository[F[_] : Applicative] extends Repository[F] {

  override def getList: F[List[String]] = Applicative[F].pure {
    List("Kimetsu no Yaiba", "RWBY", "The Attack of Titans")
  }

}
