package com.github.xelamanster.beanconverter

import com.github.xelamanster.beanconverter.model.BeanReaderError
import zio.IO

trait FileRowReader[S <: FileSettings] {
  def read(settings: S): IO[BeanReaderError, List[List[String]]]
}
