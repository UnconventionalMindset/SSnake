package io.github.jacopogobbi.ssnake

import enumeratum._

sealed trait Direction extends EnumEntry

object Direction extends Enum[Direction] {
  val values: IndexedSeq[Direction] = findValues

  case object Left extends Direction

  case object Right extends Direction

  case object Up extends Direction

  case object Down extends Direction
}

