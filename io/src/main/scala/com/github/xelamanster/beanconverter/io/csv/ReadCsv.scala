package com.github.xelamanster.beanconverter.io.csv

import java.io.File

import com.github.tototoshi.csv.CSVReader
import com.github.xelamanster.beanconverter.{BeanReaderError, FileReadError}
import com.github.xelamanster.beanconverter.io.csv.CsvSettings
import com.github.xelamanster.beanconverter.BeanConverter.ReadFileRow
import zio.IO

object ReadCsv extends ReadFileRow[CsvSettings] {

  override def apply(settings: CsvSettings): IO[BeanReaderError, List[List[String]]] = {
    def readFile = CSVReader.open(new File(settings.fileName)).all()

    IO.effect(readFile)
      .mapError(FileReadError.apply)
  }

}
