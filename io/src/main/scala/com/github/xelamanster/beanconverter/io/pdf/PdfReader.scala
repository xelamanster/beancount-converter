package com.github.xelamanster.beanconverter.io.pdf

import java.io.File

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
// import cats.implicits._

import scala.util.Using
// import scala.reflect.ClassTag
import com.github.xelamanster.beanconverter.FileRowReader
// import zio.ZIO
import zio.IO
// import cats.data._
import com.github.xelamanster.beanconverter.model.{BeanReaderError, FileReadError}
// import com.github.xelamanster.beanconverter.parser.{TypeError, TypedIterableParser, TypedParserError}
// import com.github.xelamanster.beanconverter.parser.TypedIterableParser.implicits._
// import shapeless.HList

import scala.util.matching.Regex

object PdfReader extends FileRowReader[PdfSettings] {

//  override def read(settings: Seq[PdfSettings[T]]): IO[BeanReaderError, List[T]] = {
//    ZIO.foreach(settings) { setting =>
//      ZIO.foreach(setting.sourseFilesPaths) { filename =>
//        read(setting.parser)(filename)
//      }
//
//    }
//    ZIO.fromEither(
//      settings
//        .map(setting => setting -> setting.sourseFilesPaths)
//        .map {
//          case (setting, sources) =>
//            sources
//              .map(read(setting.parser))
//              .reduce(_ |+| _)
//              .map(_ ++ setting.missing)
//        }
//        .reduce(_ |+| _)
//        .map(_.toList)
//    )
  override def read(settings: PdfSettings): IO[BeanReaderError, List[List[String]]] =
    getLines(settings.fileName)
      .map(parse(settings.regex.r))

  private def parse(regex: Regex)(rows: IndexedSeq[String]) =
    rows
      .filter(regex.matches)
      .map(regex.findAllIn(_).matchData.flatMap(_.subgroups).toList)
      .toList
//  private def read(parser: PdfParser[T])(filename: String): IO[BeanReaderError, List[T]] =
//    getLines(filename).flatMap { lines =>
//      for {
//        rawRows <- rows.toEither.leftMap[NonEmptyChain[TypedParserError]](e =>
//          cats.data.NonEmptyChain.one(TypeError(e.getMessage))
//        )
//        t <- rawRows.map(cellContent).map(decodeRaw).toList.sequence.toEither
//      ZIO.fromEither[NonEmptyChain[Throwable], List[T]](
//        lines
//          .filter(_.matches(parser.regex))
//          .map(parser.regex.r.findAllIn(_).matchData.flatMap(_.subgroups).toList)
//          .map(decodeRaw)
//          .toSet
//          .toList
//          .sequence
//          .toEither
//      )
//    }

//      content
//        .split(stripper.getLineSeparator)
//        .filter(_.matches(parser.regex))
//        .map(parser.parse)
//        .toSet
//    }.toEither

  private def getLines(filename: String): IO[FileReadError, IndexedSeq[String]] =
    IO.fromTry {
        Using(PDDocument.load(new File(filename))) { pdf =>
          val stripper = new PDFTextStripper()
          stripper
            .getText(pdf)
            .split(stripper.getLineSeparator)
            .toIndexedSeq
        }
      }
      .mapError(FileReadError.apply)
//
//  private def decodeRaw(values: Seq[String]): ValidatedNec[TypedParserError, T] = values.parse[T]
}
