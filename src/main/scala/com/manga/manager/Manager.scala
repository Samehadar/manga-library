package com.manga.manager

import com.manga.model.LibraryData

trait Manager[F[_]] {

  def get: F[LibraryData]

}
