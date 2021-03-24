package com.github.xelamanster.beanconverter

import com.github.xelamanster.beanconverter.model.Transaction.given
import com.github.xelamanster.beanconverter.BeanConverter.ConvertRow
import com.github.xelamanster.beanconverter.BeanConverter.ReadFileRow
import com.github.xelamanster.beanconverter.model.{Printer, Transaction, Row}
import com.github.xelamanster.beanconverter.parser.Parser
import cats.implicits.*

import Converter.*

class Converter[S <: FileSettings](readRow: ReadFileRow[S]):

  def convert[T <: Row: Parser](
      convertRow: ConvertRow[T],
      settings: Settings[T, S],
  ): ParsingResult =
    val readSettings = settings.readSettings
    val converter = new BeanConverter(readRow, convertRow)
    readSettings.filesSettings
      .foldLeft[Either[Throwable, List[Transaction]]](Right(List.empty)) {
        case (r, file) => r |+| converter.convert(readSettings.contentSettings, file)
      }
      .fold(f => ParsingResult.Failure(List(f)), t => ParsingResult.Success(t))

object Converter:

  def apply[S <: FileSettings](readRow: ReadFileRow[S]): Converter[S] = Converter(readRow)

  enum ParsingResult:
    case Success(transactions: List[Transaction])
    case Failure(failures: List[Throwable])

    def print(printer: Printer, writeSettings: WriteSettings): PrintResult = this match
      case Success(t) => printer.print(t, writeSettings) match 
        case Right(()) => PrintResult.Success
        case Left(f) => PrintResult.Failure(List(f))
      case Failure(f) => PrintResult.Failure(f)

    def merge(other: ParsingResult, replaceCheck: (Transaction, Transaction) => Boolean): ParsingResult = this match
      case Success(t) => other match 
        case Success(t2) => 
          val mergedT =
            t.foldLeft(List.empty[Transaction]) { case (list, t) =>
              fallbackTo(t, t2, replaceCheck) :: list
            }
            .reverse
          ParsingResult.Success(mergedT)
        case Failure(f2) => ParsingResult.Failure(f2)
      case Failure(f) => ParsingResult.Failure(f)

  end ParsingResult

  enum PrintResult:
    case Success
    case Failure(failures: List[Throwable])

  //TODO move?
  def merge[
      T <: Row: Parser,
      S <: FileSettings,
      T2 <: Row: Parser,
      S2 <: FileSettings
  ](
      mergeSettings: MergeSettings[T, S, T2, S2],
      readRow: ReadFileRow[S],
      convertRow: ConvertRow[T],
      readFallbackRow: ReadFileRow[S2],
      convertFallbackRow: ConvertRow[T2],
      printer: Printer
  ): ParsingResult =
    Converter(readRow).convert(convertRow, mergeSettings.main)
      .merge(
        Converter(readFallbackRow).convert(convertFallbackRow, mergeSettings.fallback),
        mergeSettings.replaceCheck
      )

  private def fallbackTo(
      transaction: Transaction,
      to: Seq[Transaction],
      replaceCheck: (Transaction, Transaction) => Boolean
  ): Transaction =
    to
      .find(replaceCheck(transaction, _))
      .getOrElse(transaction)

end Converter


