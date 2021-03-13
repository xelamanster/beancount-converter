package com.github.xelamanster.beanconverter.io.pdf

import java.io.File

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import scala.util.Using
import com.github.xelamanster.beanconverter.BeanConverter.ReadFileRow
import com.github.xelamanster.beanconverter.{BeanReaderError, FileReadError}

import scala.util.matching.Regex

object ReadPdf extends ReadFileRow[PdfSettings] {

  override def apply(settings: PdfSettings): Either[BeanReaderError, List[List[String]]] =
    getLines(settings.fileName)
      .map(parse(settings.regex.r))

  private def parse(regex: Regex)(rows: IndexedSeq[String]) =
    rows
      .filter(regex.matches)
      .map(regex.findAllIn(_).matchData.flatMap(_.subgroups).toList)
      .toList

  private def getLines(filename: String): Either[FileReadError, IndexedSeq[String]] =
    // IO.fromTry {
      Using(PDDocument.load(new File(filename))) { pdf =>
        val stripper = new PDFTextStripper()
        stripper
          .getText(pdf)
          .split(stripper.getLineSeparator)
          .toIndexedSeq
      }.toEither.left.map(FileReadError.apply)
    // }.mapError(FileReadError.apply)
}
