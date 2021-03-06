package com.github.xelamanster.beanconverter.model

import cats.Show
import zio.ZIO
import zio.console._
import com.github.xelamanster.beanconverter.WriteSettings

trait Printer {
  def print[T: Show](
      content: Seq[T],
      settings: WriteSettings
  ): ZIO[Console, Throwable, Unit]
}
