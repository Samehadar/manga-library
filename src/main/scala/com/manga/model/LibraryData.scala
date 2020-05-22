package com.manga.model

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class LibraryData(mangas: List[Manga])

object LibraryData {
  implicit val ldEncoder: Encoder[LibraryData] = deriveEncoder[LibraryData]
}