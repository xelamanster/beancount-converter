package com.github.xelamanster.beanconverter.io

import cats.data.ValidatedNec
import cats.implicits._
import com.github.xelamanster.beanconverter.model.{BeanConverter, BeanReaderError, FileParseError}
import com.github.xelamanster.beanconverter.parser.TypedIterableParser.implicits._
import com.github.xelamanster.beanconverter.parser.{TypedIterableParser, TypedParserError}
import com.github.xelamanster.beanconverter.{FileRowReader, FileSettings}
import zio.IO

abstract class DefaultBeanConverter[T: TypedIterableParser, S <: FileSettings](fileRowReader: FileRowReader[S])
    extends BeanConverter[T, S] {

  override def readRows(fileSettings: S): IO[BeanReaderError, List[T]] =
    fileRowReader.read(fileSettings).flatMap(fileData)

  private def fileData(fileData: List[List[String]]): IO[FileParseError, List[T]] =
    IO.fromEither(
      fileData
        .map(decodeRaw)
        .sequence
        .toEither
        .leftMap(e => FileParseError(e.toList.map(_.message): _*))
    )

  private def decodeRaw(values: Seq[String]): ValidatedNec[TypedParserError, T] = values.parse[T]
}
