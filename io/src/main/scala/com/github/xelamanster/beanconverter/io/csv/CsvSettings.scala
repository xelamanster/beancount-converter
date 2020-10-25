package com.github.xelamanster.beanconverter.io.csv

import com.github.xelamanster.beanconverter.FileSettings

final case class CsvSettings(fileName: String, delimiter: Char = ',', stripHeader: Boolean = false) extends FileSettings
