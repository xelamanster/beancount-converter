package com.github.xelamanster.beanconverter.io.xlsx

import com.github.xelamanster.beanconverter.io.DefaultBeanConverter
import com.github.xelamanster.beanconverter.parser.TypedIterableParser

abstract class XlsxConverter[T: TypedIterableParser] extends DefaultBeanConverter[T, XlsxSettings](XlsxReader)
