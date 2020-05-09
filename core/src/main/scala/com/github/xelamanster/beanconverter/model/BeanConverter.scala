package com.github.xelamanster.beanconverter.model

import cats.data.NonEmptyChain
import com.github.xelamanster.beanconverter.{ContentSettings, FileSettings}
import com.github.xelamanster.beanconverter.model.operations.Operation
import cats.implicits._
import com.github.xelamanster.beanconverter.model.operations.Operation.Ignore
import zio.IO

object BeanConverter {
  def apply[T, S <: FileSettings](implicit converter: BeanConverter[T, S]): BeanConverter[T, S] = converter
}

trait BeanConverter[T, S <: FileSettings] {

  def convert(contentSettings: ContentSettings, fileSettings: S): IO[BeanConverterError, List[Transaction]] = {
    readRows(fileSettings)
      .flatMap(convertRows(contentSettings))
  }

  private def convertRows(contentSettings: ContentSettings)(rows: List[T]) = {

    def fromRow(row: T) =
      findTarget(toDescription(row), contentSettings)
        .fold(
          ConvertionError(s"no target found for [${toDescription(row)}]").invalidNec[List[Transaction]]
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

  protected def convertRow(v: T, source: Account, operation: Operation): Transaction

  protected def readRows(fileSettings: S): IO[BeanReaderError, List[T]]

  protected def toDescription(t: T): String

  private def findTarget(description: String, settings: ContentSettings) = {

    val lowerDescription = description.toLowerCase

    def descriptionContains(collection: Map[Operation, Seq[String]]) =
      collection.find {
        case (_, values) => values.exists(lowerDescription.contains)
      }

    descriptionContains(
      settings.targetMapping.view.mapValues(_.map(_.toLowerCase())).toMap
    ).orElse(
        descriptionContains(
          settings.miscMapping.view.mapValues(_.map(_.toLowerCase())).toMap
        )
      )
      .map {
        case (operationType, _) =>
          operationType
      }
  }
}
