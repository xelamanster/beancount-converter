package com.github.xelamanster.beanconverter.io.pdf

//import com.github.xelamanster.beanconverter.model.PdfTransaction

//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//import com.github.xelamanster.beanconverter.model.Currency

//object PdfParser {

//  private val defaultTimeFormat = "d/MM/yyyy"

//  private val DefaultRegex =
//    "\\s*-\\s+(\\d{1,2}\\/\\d{2}\\/\\d{4})\\s.+\\s{2,}(-?\\d+.\\d+)(\\s+\\d+){3}\\s+(.+)\\s{3,}(.+)".r

//  def defaultPdfParser(currency: Currency.Value) =
//    new PdfParser[PdfTransaction] {
//      def parse(entry: String): PdfTransaction = entry match {
//        case DefaultRegex(date, amount, _, description, comment) =>
//          PdfTransaction(
//            LocalDate
//              .parse(date, DateTimeFormatter.ofPattern(defaultTimeFormat)),
//            BigDecimal.apply(amount.toDouble),
//            currency,
//            description.trim,
//            comment
//          )
//
//      }
//
//      def regex: String =
//        DefaultRegex.regex
//    }
//
//  def apply[T: PdfParser]: PdfParser[T] =
//    implicitly[PdfParser[T]]
//}

//trait PdfParser[T] {
//  def parse(entry: String): T
//  def regex: String
//}
