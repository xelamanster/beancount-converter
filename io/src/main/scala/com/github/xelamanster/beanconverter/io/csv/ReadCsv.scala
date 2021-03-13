package com.github.xelamanster.beanconverter.io.csv

import com.github.xelamanster.beanconverter.{BeanReaderError, FileReadError}
import com.github.xelamanster.beanconverter.io.csv.CsvSettings
import com.github.xelamanster.beanconverter.BeanConverter.ReadFileRow

import com.github.tototoshi.csv.CSVReader
import com.github.tototoshi.csv.DefaultCSVFormat
import java.io.File

object ReadCsv extends ReadFileRow[CsvSettings] {

  private val defaultEmpty = ""

  override def apply(settings: CsvSettings): Either[BeanReaderError, List[List[String]]] = {
    val format = new DefaultCSVFormat {
      override val delimiter = settings.delimiter
    }

    val wholeFile = CSVReader.open(new File(settings.fileName))(format).all()

    val data = if (settings.stripHeader) wholeFile.tail else wholeFile

    if (settings.empty != defaultEmpty) data.map(_.map(replaceEmpty(settings.empty))) else data

    // .mapError(FileReadError.apply)
    ???
  }

  private def replaceEmpty(empty: String)(v: String) =
    if (v == empty) defaultEmpty
    else v

}
