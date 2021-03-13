package com.github.xelamanster.beanconverter.model

import cats.Show
import com.github.xelamanster.beanconverter.WriteSettings

trait Printer {
  def print[T: Show](
      content: Seq[T],
      settings: WriteSettings
  ): Either[Throwable, Unit]
}
