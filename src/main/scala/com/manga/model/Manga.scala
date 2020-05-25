package com.manga.model

import java.time.LocalDate

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.generic.semiauto.deriveDecoder

case class Manga(id: Option[Long], title: String, releaseDate: LocalDate, authorId: Long)

object Manga {
  implicit val mangaEncoder: Encoder[Manga] = deriveEncoder[Manga]
  implicit val mangaDecoder: Decoder[Manga] = deriveDecoder[Manga]
}

case object MangaNotFoundError {
  implicit val encodeImportance: Encoder[MangaNotFoundError.type] = deriveEncoder[MangaNotFoundError.type]

  implicit val decodeImportance: Decoder[MangaNotFoundError.type] = deriveDecoder[MangaNotFoundError.type]

}

