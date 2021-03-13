package com.github.xelamanster.beanconverter.io.csv

import com.github.xelamanster.beanconverter.{BeanReaderError, FileReadError}
import com.github.xelamanster.beanconverter.io.csv.CsvSettings
import com.github.xelamanster.beanconverter.BeanConverter.ReadFileRow

import com.github.tototoshi.csv.CSVReader
import com.github.tototoshi.csv.DefaultCSVFormat
import java.io.File

import scala.util.Using

object ReadCsv extends ReadFileRow[CsvSettings] {

  private val defaultEmpty = ""

  override def apply(settings: CsvSettings): Either[BeanReaderError, List[List[String]]] = {
    val format = new DefaultCSVFormat {
      override val delimiter = settings.delimiter
    }

    Using(CSVReader.open(new File(settings.fileName))(format)) { reader =>
      val wholeFile = reader.all()
      val data =
        if settings.stripHeader then wholeFile.tail
        else wholeFile

      if settings.empty != defaultEmpty then data.map(_.map(replaceEmpty(settings.empty)))
      else data
      
    }.toEither.left.map(FileReadError.apply)
  }

  private def replaceEmpty(empty: String)(v: String) =
    if (v == empty) defaultEmpty
    else v

}
