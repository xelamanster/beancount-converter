package com.github.xelamanster.beanconverter.io

import java.io.{File, FileWriter}

import cats.Show
import com.github.xelamanster.beanconverter.model.Printer
import zio.ZIO
import zio.console.{Console, putStrLn}

import scala.util.Using

import cats.implicits._
import com.github.xelamanster.beanconverter.ExportSettings

package object printer {
  object ConsolePrinter extends Printer {

    override def print[T: Show](
        content: Seq[T],
        settings: ExportSettings
    ): ZIO[Console, Throwable, Unit] = {
      val formattedContent =
        content
          .map(_.show)
          .mkString(System.lineSeparator() * 2)

      putStrLn(formattedContent)
    }
  }

  object DullConsolePrinter extends Printer {

    override def print[T: Show](
        content: Seq[T],
        settings: ExportSettings
    ): ZIO[Console, Throwable, Unit] =
      putStrLn("")
  }

  object FilePrinter extends Printer {

    override def print[T: Show](
        content: Seq[T],
        settings: ExportSettings
    ): ZIO[Console, Throwable, Unit] = {
      val file = new File(settings.targetFilename)
      ZIO.fromTry(
        Using(new FileWriter(file))(
          _.write(content.map(_.show).mkString(System.lineSeparator() * 2))
        )
      )
    }

  }
}
