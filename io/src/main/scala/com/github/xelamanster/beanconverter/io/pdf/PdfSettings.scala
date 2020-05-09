package com.github.xelamanster.beanconverter.io.pdf

import com.github.xelamanster.beanconverter.FileSettings

//case class PdfSettings[T](
//    sourseFilesPaths: Seq[String],
//    missing: Seq[T],
//    parser: PdfParser[T]
//) extends FileSettings

case class PdfSettings(
    fileName: String,
    regex: String
) extends FileSettings
