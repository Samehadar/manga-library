package com.manga.util

import java.time.LocalDateTime

import cats.effect.Sync

trait Time[F[_]] {
  def localDateTime: F[LocalDateTime]
}

object Time {
  def apply[F[_] : Time]: Time[F] = implicitly[Time[F]]

  implicit def syncTime[F[_] : Sync]: Time[F] = new Time[F] {
    override def localDateTime: F[LocalDateTime] = Sync[F].delay(LocalDateTime.now())
  }
}