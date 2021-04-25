package com.github.xelamanster.beanconverter

import cats.data.NonEmptyChain
import cats.data.ValidatedNec
import com.github.xelamanster.beanconverter.model._
import com.github.xelamanster.beanconverter.{ContentSettings, FileSettings}
import com.github.xelamanster.beanconverter.model.operations.Operation
import cats.implicits._
import com.github.xelamanster.beanconverter.model.operations.Operation.Ignore
import com.github.xelamanster.beanconverter.parser.{Parser, DecodeError}
import BeanConverter.{ReadFileRow, ConvertRow}

object BeanConverter {
  type ReadFileRow[S <: FileSettings] = S => Either[BeanReaderError, List[List[String]]]
  type ConvertRow[R <: Row] = (R, Account, Operation) => Transaction

  def apply[R <: Row, S <: FileSettings](using converter: BeanConverter[R, S]): BeanConverter[R, S] = converter
}

class BeanConverter[R <: Row: Parser, S <: FileSettings](
    readFileRow: ReadFileRow[S],
    convertRow: ConvertRow[R]
) {

  def convert(contentSettings: ContentSettings, fileSettings: S): Either[BeanConverterError, List[Transaction]] =
    readRows(fileSettings)
      .flatMap(convertRows(contentSettings))

  private def readRows(fileSettings: S): Either[BeanReaderError, List[R]] =
    readFileRow(fileSettings).flatMap(fileData)

  private def fileData(fileData: List[List[String]]): Either[FileParseError, List[R]] =
      fileData
        .map(decodeRaw)
        .sequence
        .toEither
        .leftMap(e => FileParseError(e.toList.map(_.message): _*))

  private def decodeRaw(values: Seq[String]): ValidatedNec[DecodeError, R] = summon[Parser[R]].parse(values)

  private def convertRows(contentSettings: ContentSettings)(rows: List[R]) = {

    def fromRow(row: R) =
      findTarget(row.description, contentSettings)
        .fold(
          ConversionError(s"no target found for [${row.description}]").invalidNec[List[Transaction]]
        ) {
          case Ignore => List.empty.validNec
          case o =>
            List(convertRow(row, contentSettings.sourceAccount, o)).validNec
        }

    def combineErrors(errors: NonEmptyChain[ConversionError]): ConversionError =
      ConversionError(errors.toList.flatMap(_.messages): _*)

    rows
      .flatMap(fromRow(_).sequence)
      .sequence
      .toEither
      .leftMap(combineErrors)
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
