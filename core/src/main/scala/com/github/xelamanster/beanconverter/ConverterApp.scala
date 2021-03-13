package com.github.xelamanster.beanconverter

import com.github.xelamanster.beanconverter.model.Transaction.implicits._
import com.github.xelamanster.beanconverter.BeanConverter.ConvertRow
import com.github.xelamanster.beanconverter.BeanConverter.ReadFileRow
import com.github.xelamanster.beanconverter.model.{Printer, Transaction, Row}
import com.github.xelamanster.beanconverter.parser.Parser

object ConverterApp {

  def convert[T <: Row: Parser, S <: FileSettings](
      settings: Seq[Settings[T, S]],
      readRow: ReadFileRow[S],
      convertRow: ConvertRow[T],
      printer: Printer
  ): Unit =
    settings.map(loop(readRow, convertRow, printer)).foreach {
      case Left(e) => println(e)
      case Right(_) =>
    }

  private def loop[T <: Row: Parser, S <: FileSettings](
      readRow: ReadFileRow[S],
      convertRow: ConvertRow[T],
      printer: Printer
  )(settings: Settings[T, S]) =
    for {
      transactions <- readTransactions(readRow, convertRow, settings.readSettings)
      _ <- printer.print(transactions, settings.writeSettings)
    } yield ()

  private def readTransactions[T <: Row: Parser, S <: FileSettings](
      readRow: ReadFileRow[S],
      convertRow: ConvertRow[T],
      settings: ReadSettings[T, S]
  ): Either[Throwable, List[Transaction]] = {
    val converter = new BeanConverter(readRow, convertRow)
    settings.filesSettings
      .foldLeft[Either[Throwable, List[Transaction]]](Right(List.empty)) { case (r, file) =>
        for {
          res <- r
          t <- converter.convert(settings.contentSettings, file)
        } yield res ++ t
      }
  }

  def merge[
      T <: Row: Parser,
      S <: FileSettings,
      T2 <: Row: Parser,
      S2 <: FileSettings
  ](
      mergeSettings: MergeSettings[T, S, T2, S2],
      readRow: ReadFileRow[S],
      convertRow: ConvertRow[T],
      readFalbackRow: ReadFileRow[S2],
      convertFalbackRow: ConvertRow[T2],
      printer: Printer
  ): Unit =
    (for {
      transactions <- readTransactions(readRow, convertRow, mergeSettings.readSettings)
      fallback <- readTransactions(readFalbackRow, convertFalbackRow, mergeSettings.readSettingsFallback)
      merged =
        transactions
          .foldLeft(List.empty[Transaction]) { case (list, t) =>
            fallbackTo(t, fallback, mergeSettings.replaceCheck) :: list
          }
          .reverse
      _ <- printer.print(merged, mergeSettings.exportSettings)
    } yield ()).left.foreach(println)

  private def fallbackTo(
      transaction: Transaction,
      to: Seq[Transaction],
      replaceCheck: (Transaction, Transaction) => Boolean
  ): Transaction =
    to
      .find(replaceCheck(transaction, _))
      .getOrElse(transaction)
}
