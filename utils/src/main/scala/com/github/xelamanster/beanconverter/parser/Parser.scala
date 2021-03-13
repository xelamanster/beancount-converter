package com.github.xelamanster.beanconverter.parser

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import cats.data.ValidatedNec
import cats.implicits._

import scala.util.Try

import scala.deriving.*

trait Parser[X]:
  def parse(from: Iterable[String]): ValidatedNec[DecodeError, X]

object Parser:
  
  def create[X](using m: Mirror.ProductOf[X], d: RawDecoder[m.MirroredElemTypes]) =
    new Parser[X]:
      def parse(from: Iterable[String]): ValidatedNec[DecodeError, X] = Parser.parse(from).toValidatedNec

  def parseTuple[X <: Tuple : RawDecoder](raw: Raw): Either[DecodeError, X] =
    summon[RawDecoder[X]].decode(raw)
  
  def parse[X](raw: Raw)(using m: Mirror.ProductOf[X], d: RawDecoder[m.MirroredElemTypes]): Either[DecodeError, X] =
    parseTuple[m.MirroredElemTypes](raw).map(m.fromProduct)

end Parser
  
type RawField = String
type Raw = Iterable[RawField]
case class DecodeError(message: String) extends Throwable

trait RawDecoder[T]:
  def decode(raw: Raw): Either[DecodeError, T]

trait FieldDecoder[T]:
  def decode(raw: RawField): Either[DecodeError, T]

object Decoders:

  given FieldDecoder[String] with
    def decode(raw: RawField): Either[DecodeError, String] =
      Right(raw) 

  given FieldDecoder[Boolean] with
    def decode(raw: RawField): Either[DecodeError, Boolean] =
      raw.toBooleanOption.toRight(DecodeError(s"Cant parse Boolean from [$raw]"))

  given FieldDecoder[Int] with
    def decode(raw: RawField): Either[DecodeError, Int] =
      raw.toIntOption.toRight(DecodeError(s"Cant parse Int from [$raw]"))

  given RawDecoder[EmptyTuple] with
    def decode(remain: Raw): Either[DecodeError, EmptyTuple] = 
      if remain.isEmpty then Right(EmptyTuple)
      else Left(DecodeError(s"Left unparsed [$remain]"))

  given [H: FieldDecoder, T <: Tuple : RawDecoder]: RawDecoder[H *: T] with
    def decode(raw: Raw): Either[DecodeError, H *: T] =
      for {
        t1 <- summon[FieldDecoder[H]].decode(raw.head)
        t2 <- summon[RawDecoder[T]].decode(raw.tail)
      } yield Tuple(t1) ++ t2

end Decoders

// object Parser {
//   def apply[T: Parser]: Parser[T] = implicitly[Parser[T]]

//   object implicits {
//     implicit val intParser: Parser[Int] = _.toIntOption

//     implicit val doubleParser: Parser[Double] =
//       raw =>
//         if (raw.isEmpty) Option(0.0)
//         else raw.toDoubleOption

//     implicit val bigDecimalParser: Parser[BigDecimal] =
//       raw => {
//         val prepared =
//           if (raw.contains(",")) raw.replace(",", ".")
//           else raw

//         doubleParser
//           .parse(prepared)
//           .map(BigDecimal.apply)
//       }

//     implicit val dateParser: Parser[LocalDate] =
//       rawDate => {
//         def parse(pattern: String) = LocalDate.parse(rawDate, DateTimeFormatter.ofPattern(pattern))

//         Try(parse("dd.MM.yyyy"))
//           .orElse(Try(parse("d/MM/yyyy")))
//           .orElse(Try(parse("yyyy/MM/dd")))
//           .orElse(Try(parse("MMM d, yyyy ")))
//           .toOption

//       }

//     implicit val dateTimeParser: Parser[LocalDateTime] =
//       raw => Try(LocalDateTime.parse(raw, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).toOption

//     implicit val stringParser: Parser[String] = Option(_)
//   }

// }
