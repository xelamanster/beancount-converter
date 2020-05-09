package com.github.xelamanster.beanconverter.io.csv

import com.github.xelamanster.beanconverter.CsvSettings
import com.github.xelamanster.beanconverter.io.DefaultBeanConverter
import com.github.xelamanster.beanconverter.parser.TypedIterableParser

abstract class CsvConverter[T: TypedIterableParser] extends DefaultBeanConverter[T, CsvSettings](CsvReader)
