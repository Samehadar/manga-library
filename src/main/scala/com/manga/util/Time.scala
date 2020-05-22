package com.manga.util

import java.time.LocalDate

import cats.effect.Sync

trait Time[F[_]] {
  def localDate: F[LocalDate]
}

object Time {
  def apply[F[_] : Time]: Time[F] = implicitly[Time[F]]

  implicit def syncTime[F[_] : Sync]: Time[F] = new Time[F] {
    override def localDate: F[LocalDate] = Sync[F].delay(LocalDate.now())
  }
}