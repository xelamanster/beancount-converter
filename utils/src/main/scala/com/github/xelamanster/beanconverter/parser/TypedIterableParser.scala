package com.github.xelamanster.beanconverter.parser

import cats.data.ValidatedNec
import cats.implicits._

import scala.reflect.ClassTag

trait TypedIterableParser[R] {
  def parse(from: Iterable[String]): ValidatedNec[TypedParserError, R]
}

// object TypedIterableParser {

//   def apply[T: TypedIterableParser]: TypedIterableParser[T] = implicitly[TypedIterableParser[T]]

//   object implicits {

//     implicit class TypedIterableParserSyntax(from: Iterable[String]) {
//       def parse[T](implicit ftp: TypedIterableParser[T]): ValidatedNec[TypedParserError, T] = ftp.parse(from)
//     }

//     implicit val hNilParsed: TypedIterableParser[HNil] =
//       from =>
//         if (from.isEmpty) HNil.validNec
//         else TypeError(s"Collection is not empty: [$from]").invalidNec

//     implicit def genParsed[OutH: Parser: ClassTag, OutT <: HList: TypedIterableParser]: TypedIterableParser[
//       OutH :: OutT
//     ] =
//       from =>
//         if (from.isEmpty) TypeError(s"Collection is empty: [$from]").invalidNec
//         else (parse(from.head), from.tail.parse[OutT]).mapN(_ :: _)

//     implicit def genClass[A, R](implicit gen: Generic.Aux[A, R], conv: TypedIterableParser[R]): TypedIterableParser[A] =
//       from => conv.parse(from).map(gen.from)

//     private def parse[T: ClassTag: Parser](value: String): ValidatedNec[TypedParserError, T] =
//       Parser[T].parse(value) match {
//         case Some(value) => value.validNec
//         case None =>
//           val clazz = implicitly[reflect.ClassTag[T]].runtimeClass
//           ParsingError(s"Can't parse $clazz from [$value]").invalidNec
//       }
//   }

// }
