package com.github.xelamanster.beanconverter.io

import java.io.{File, FileWriter}

import cats.Show
import com.github.xelamanster.beanconverter.model.Printer

import scala.util.Using
import scala.util.Try

import cats.implicits._
import com.github.xelamanster.beanconverter.WriteSettings

package object printer {
  object ConsolePrinter extends Printer {

    override def print[T: Show](
        content: Seq[T],
        settings: WriteSettings
    ): Either[Throwable, Unit] = {
      val formattedContent =
        content
          .map(_.show)
          .mkString(System.lineSeparator() * 2)

      Try(println(formattedContent)).toEither
    }
  }

  object DullConsolePrinter extends Printer {

    override def print[T: Show](
        content: Seq[T],
        settings: WriteSettings
    ): Either[Throwable, Unit] =
      Right(())
  }

  object FilePrinter extends Printer {

    override def print[T: Show](
        content: Seq[T],
        settings: WriteSettings
    ): Either[Throwable, Unit] = {
      val file = new File(settings.filename)

      Using(new FileWriter(file))(
        _.write(content.map(_.show).mkString(System.lineSeparator() * 2))
      ).toEither
    }

  }
}
