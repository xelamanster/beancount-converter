package com.github.xelamanster.beanconverter.parser

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import scala.util.Try

trait Parser[T] {
  def parse(raw: String): Option[T]
}

object Parser {
  def apply[T: Parser]: Parser[T] = implicitly[Parser[T]]

  object implicits {
    implicit val intParser: Parser[Int] = _.toIntOption

    implicit val doubleParser: Parser[Double] =
      raw =>
        if (raw.isEmpty) Option(0.0)
        else raw.toDoubleOption

    implicit val bigDecimalParser: Parser[BigDecimal] =
      raw => {
        val prepared =
          if (raw.contains(",")) raw.replace(",", ".")
          else raw

        doubleParser
          .parse(prepared)
          .map(BigDecimal.apply)
      }

    implicit val dateParser: Parser[LocalDate] =
      rawDate => {
        def parse(pattern: String) = LocalDate.parse(rawDate, DateTimeFormatter.ofPattern(pattern))

        Try(parse("dd.MM.yyyy"))
          .orElse(Try(parse("d/MM/yyyy")))
          .toOption

      }

    implicit val dateTimeParser: Parser[LocalDateTime] =
      raw => Try(LocalDateTime.parse(raw, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).toOption

    implicit val stringParser: Parser[String] = Option(_)
  }

}
