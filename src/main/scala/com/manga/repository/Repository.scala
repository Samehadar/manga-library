package com.manga.repository

import cats.effect.IO

trait Repository[F[_]] {
  def getList: F[List[String]]
}
