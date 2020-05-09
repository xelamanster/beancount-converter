package com.github.xelamanster.beanconverter.io.csv

import java.io.File

import com.github.tototoshi.csv.CSVReader
import com.github.xelamanster.beanconverter.model.{BeanReaderError, FileReadError}
import com.github.xelamanster.beanconverter.{CsvSettings, FileRowReader}
import zio.IO

object CsvReader extends FileRowReader[CsvSettings] {

  override def read(settings: CsvSettings): IO[BeanReaderError, List[List[String]]] = {
    def readFile = CSVReader.open(new File(settings.fileName)).all()

    IO.effect(readFile)
      .mapError(FileReadError.apply)
  }

}
