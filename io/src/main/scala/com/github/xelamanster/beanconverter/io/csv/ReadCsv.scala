package com.github.xelamanster.beanconverter.io.csv

import com.github.xelamanster.beanconverter.{BeanReaderError, FileReadError}
import com.github.xelamanster.beanconverter.io.csv.CsvSettings
import com.github.xelamanster.beanconverter.BeanConverter.ReadFileRow

import com.github.tototoshi.csv.CSVReader
import com.github.tototoshi.csv.DefaultCSVFormat
import zio.IO
import java.io.File

object ReadCsv extends ReadFileRow[CsvSettings] {

  private val defaultEmpty = ""

  override def apply(settings: CsvSettings): IO[BeanReaderError, List[List[String]]] = {
    val format = new DefaultCSVFormat {
      override val delimiter = settings.delimiter
    }
    def wholeFile = CSVReader.open(new File(settings.fileName))(format).all()

    IO.effect(if (settings.stripHeader) wholeFile.tail else wholeFile)
      .map(list => if (settings.empty != defaultEmpty) list.map(_.map(replaceEmpty(settings.empty))) else list)
      .mapError(FileReadError.apply)
  }

  private def replaceEmpty(empty: String)(v: String) =
    if (v == empty) defaultEmpty
    else v

}
