package com.manga.manager

import com.manga.model.LibraryData

trait Manager[F[_], A] {

  def get: F[LibraryData]

  def get(id: Long): F[Option[A]]

  def add(a: A): F[A]

  def delete(id: Long): F[Boolean]

}
