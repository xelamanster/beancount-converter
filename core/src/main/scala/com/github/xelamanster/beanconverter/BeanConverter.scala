package com.github.xelamanster.beanconverter

import cats.data.NonEmptyChain
import cats.data.ValidatedNec
import com.github.xelamanster.beanconverter.model._
import com.github.xelamanster.beanconverter.{ContentSettings, FileSettings}
import com.github.xelamanster.beanconverter.model.operations.Operation
import cats.implicits._
import com.github.xelamanster.beanconverter.parser.TypedIterableParser.implicits._
import com.github.xelamanster.beanconverter.model.operations.Operation.Ignore
import com.github.xelamanster.beanconverter.parser.{TypedIterableParser, TypedParserError}
import BeanConverter.{ReadFileRow, ConvertRow}

import zio.IO

object BeanConverter {
  type ReadFileRow[S <: FileSettings] = S => IO[BeanReaderError, List[List[String]]]
  type ConvertRow[R <: Row] = (R, Account, Operation) => Transaction

  def apply[R <: Row, S <: FileSettings](implicit converter: BeanConverter[R, S]): BeanConverter[R, S] = converter
}

class BeanConverter[R <: Row: TypedIterableParser, S <: FileSettings](
    readFileRow: ReadFileRow[S],
    convertRow: ConvertRow[R]
) {

  def convert(contentSettings: ContentSettings, fileSettings: S): IO[BeanConverterError, List[Transaction]] =
    readRows(fileSettings)
      .flatMap(convertRows(contentSettings))

  private def readRows(fileSettings: S): IO[BeanReaderError, List[R]] =
    readFileRow(fileSettings).flatMap(fileData)

  private def fileData(fileData: List[List[String]]): IO[FileParseError, List[R]] =
    IO.fromEither(
      fileData
        .map(decodeRaw)
        .sequence
        .toEither
        .leftMap(e => FileParseError(e.toList.map(_.message): _*))
    )

  private def decodeRaw(values: Seq[String]): ValidatedNec[TypedParserError, R] = values.parse[R]

  private def convertRows(contentSettings: ContentSettings)(rows: List[R]) = {

    def fromRow(row: R) =
      findTarget(row.description, contentSettings)
        .fold(
          ConvertionError(s"no target found for [${row.description}]").invalidNec[List[Transaction]]
        ) {
          case Ignore => List.empty.validNec
          case o =>
            List(convertRow(row, contentSettings.sourceAccount, o)).validNec
        }

    def combineErrors(errors: NonEmptyChain[ConvertionError]) =
      ConvertionError(errors.toList.flatMap(_.messages): _*)

    IO.fromEither(
      rows
        .flatMap(fromRow(_).sequence)
        .sequence
        .toEither
        .leftMap(combineErrors)
    )
  }

  private def findTarget(description: String, settings: ContentSettings) = {

    val lowerDescription = description.toLowerCase

    def descriptionContains(collection: Map[Operation, Seq[String]]) =
      collection.find { case (_, values) =>
        values.exists(lowerDescription.contains)
      }

    descriptionContains(
      settings.targetMapping.view.mapValues(_.map(_.toLowerCase())).toMap
    ).orElse(
      descriptionContains(
        settings.miscMapping.view.mapValues(_.map(_.toLowerCase())).toMap
      )
    ).map { case (operationType, _) =>
      operationType
    }
  }
}
