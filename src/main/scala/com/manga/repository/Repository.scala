package com.manga.repository

trait Repository[F[_]] {
  def getList: F[List[String]]
}
