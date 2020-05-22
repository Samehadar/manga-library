package com.manga.model

import java.time.LocalDate

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.generic.semiauto.deriveDecoder

case class Manga(id: Long, title: String, releaseDate: LocalDate, authorId: Long) {

}

object Manga {
  implicit val mangaEncoder: Encoder[Manga] = deriveEncoder[Manga]
  implicit val mangaDecoder: Decoder[Manga] = deriveDecoder[Manga]
}