package com.github.xelamanster.beanconverter

import com.github.xelamanster.beanconverter.model.Transaction.implicits._
import com.github.xelamanster.beanconverter.BeanConverter.ConvertRow
import com.github.xelamanster.beanconverter.BeanConverter.ReadFileRow
import com.github.xelamanster.beanconverter.model.{Printer, Transaction, Row}
import zio.{IO, ZIO}
import zio.console.Console
import com.github.xelamanster.beanconverter.parser.TypedIterableParser

object ConverterApp {

  def convert[T <: Row: TypedIterableParser, S <: FileSettings](
      settings: Seq[Settings[T, S]],
      readRow: ReadFileRow[S],
      convertRow: ConvertRow[T],
      printer: Printer
  ): ZIO[Console, Nothing, Int] =
    app(
      ZIO
        .foreach(settings)(loop(readRow, convertRow, printer))
    )

  private def app[Er, T](loop: ZIO[Console, Er, T]): ZIO[Console, Nothing, Int] =
    loop
      .mapError { e =>
        println(e); e
      }
      .fold(_ => 1, _ => 0)

  private def loop[T <: Row: TypedIterableParser, S <: FileSettings](
      readRow: ReadFileRow[S],
      convertRow: ConvertRow[T],
      printer: Printer
  )(settings: Settings[T, S]) =
    for {
      transactions <- readTransactions(readRow, convertRow, settings.readSettings)
      _ <- IO.fromEither(BeanChecker.check(transactions))
      _ <- printer.print(transactions, settings.writeSettings)
    } yield ()

  private def readTransactions[T <: Row: TypedIterableParser, S <: FileSettings](
      readRow: ReadFileRow[S],
      convertRow: ConvertRow[T],
      settings: ReadSettings[T, S]
  ) = {
    val converter = new BeanConverter(readRow, convertRow)
    IO.foreach(settings.filesSettings)(converter.convert(settings.contentSettings, _))
      .map(_.flatten)
  }

  def merge[
      T <: Row: ConvertRow: TypedIterableParser,
      S <: FileSettings: ReadFileRow,
      T2 <: Row: ConvertRow: TypedIterableParser,
      S2 <: FileSettings: ReadFileRow
  ](
      mergeSettings: MergeSettings[T, S, T2, S2],
      readRow: ReadFileRow[S],
      convertRow: ConvertRow[T],
      readFalbackRow: ReadFileRow[S2],
      convertFalbackRow: ConvertRow[T2],
      printer: Printer
  ): ZIO[Console, Nothing, Int] =
    app(
      for {
        transactions <- readTransactions(readRow, convertRow, mergeSettings.readSettings)
        fallback <- readTransactions(readFalbackRow, convertFalbackRow, mergeSettings.readSettingsFallback)
        merged =
          transactions
            .foldLeft(List.empty[Transaction]) { case (list, t) =>
              fallbackTo(t, fallback, mergeSettings.replaceCheck) :: list
            }
            .reverse
        _ <- IO.fromEither(BeanChecker.check(merged))
        _ <- printer.print(merged, mergeSettings.exportSettings)
      } yield ()
    )

  private def fallbackTo(
      transaction: Transaction,
      to: Seq[Transaction],
      replaceCheck: (Transaction, Transaction) => Boolean
  ): Transaction =
    to
      .find(replaceCheck(transaction, _))
      .getOrElse(transaction)
}
