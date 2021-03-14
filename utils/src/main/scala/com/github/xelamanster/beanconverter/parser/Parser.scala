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
      raw.toBooleanOption.toRight(DecodeError(s"Can't parse Boolean from [$raw]"))

  given FieldDecoder[Int] with
    def decode(raw: RawField): Either[DecodeError, Int] =
      raw.toIntOption.toRight(DecodeError(s"Can't parse Int from [$raw]"))

  given FieldDecoder[Double] with
    def decode(raw: RawField): Either[DecodeError, Double] =
      if raw.isEmpty then Right(0.0)
      else raw.toDoubleOption.toRight(DecodeError(s"Can't parse Double from [$raw]"))

  given FieldDecoder[BigDecimal] with
    def decode(raw: RawField): Either[DecodeError, BigDecimal] =
      val prepared =
        if raw.contains(",") then raw.replace(",", ".")
        else raw

      summon[FieldDecoder[Double]]
        .decode(prepared)
        .map(BigDecimal.apply)
  end given

  given FieldDecoder[LocalDateTime] with
    def decode(raw: RawField): Either[DecodeError, LocalDateTime] =
      Try(LocalDateTime.parse(raw, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).toEither.left.map(_ => DecodeError(s"Can't parse LocalDateTime from [$raw]"))

  given FieldDecoder[LocalDate] with
    def decode(raw: RawField): Either[DecodeError, LocalDate] =
      def parse(pattern: String) = LocalDate.parse(raw, DateTimeFormatter.ofPattern(pattern))
      Try(parse("dd.MM.yyyy"))
        .orElse(Try(parse("d/MM/yyyy")))
        .orElse(Try(parse("yyyy/MM/dd")))
        .orElse(Try(parse("MMM d, yyyy ")))
        .toOption
        .toRight(DecodeError(s"Can't parse LocalDate from [$raw]"))

  given RawDecoder[EmptyTuple] with
    def decode(remain: Raw): Either[DecodeError, EmptyTuple] = 
      if remain.isEmpty then Right(EmptyTuple)
      else Left(DecodeError(s"Left unparsed [$remain]"))

  given [H: FieldDecoder, T <: Tuple : RawDecoder]: RawDecoder[H *: T] with
    def decode(raw: Raw): Either[DecodeError, H *: T] =
      for
        t1 <- summon[FieldDecoder[H]].decode(raw.head)
        t2 <- summon[RawDecoder[T]].decode(raw.tail)
      yield Tuple(t1) ++ t2

  given [X](using m: Mirror.ProductOf[X], d: RawDecoder[m.MirroredElemTypes]): Parser[X] = Parser.create

end Decoders