package com.github.xelamanster.beanconverter

import com.github.xelamanster.beanconverter.model.Transaction.implicits._
import com.github.xelamanster.beanconverter.model.{Printer, Transaction}
import zio.{IO, ZIO}
import zio.console.Console

object ConverterApp {

  def convert[T, S <: FileSettings](
      settings: Seq[Settings[T, S]],
      printer: Printer
  ): ZIO[Console, Nothing, Int] =
    app(
      ZIO
        .foreach(settings)(loop(printer))
    )

  private def app[Er, T](loop: ZIO[Console, Er, T]): ZIO[Console, Nothing, Int] =
    loop
      .mapError { e =>
        println(e); e
      }
      .fold(_ => 1, _ => 0)

  private def loop[T, S <: FileSettings](printer: Printer)(settings: Settings[T, S]) =
    for {
      transactions <- readTransactions(settings.readSettings)
      _ <- IO.fromEither(BeanChecker.check(transactions))
      _ <- printer.print(transactions, settings.exportSettings)
    } yield ()

  private def readTransactions[T, S <: FileSettings](settings: ReadSettings[T, S]) =
    IO.foreach(settings.filesSettings)(settings.converter.convert(settings.contentSettings, _))
      .map(_.flatten)

  def merge[T, S <: FileSettings, T2, S2 <: FileSettings](
      mergeSettings: MergeSettings[T, S, T2, S2],
      printer: Printer
  ): ZIO[Console, Nothing, Int] =
    app(
      for {
        t <- readTransactions(mergeSettings.readSettings)
        withT <- readTransactions(mergeSettings.readSettingsFallback)
        merged = t
          .foldLeft(List.empty[Transaction]) {
            case (list, t) => fallbackWith(t, withT, mergeSettings.replaceCheck) :: list
          }
          .reverse
        _ <- IO.fromEither(BeanChecker.check(merged))
        _ <- printer.print(merged, mergeSettings.exportSettings)
      } yield ()
    )

  private def fallbackWith(transaction: Transaction, withT: Seq[Transaction], replaceCheck: ReplaceCheck): Transaction =
    withT.find(replaceCheck.shouldReplace(transaction)).getOrElse(transaction)
}
