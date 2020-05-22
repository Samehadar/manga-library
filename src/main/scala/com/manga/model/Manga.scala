package com.manga.model

import java.time.LocalDate

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class Manga(id: Long, title: String, releaseDate: LocalDate, authorId: Long) {

}

object Manga {
  implicit val mangaEncoder: Encoder[Manga] = deriveEncoder[Manga]
}