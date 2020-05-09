package com.github.xelamanster.beanconverter.io.pdf

import com.github.xelamanster.beanconverter.io.DefaultBeanConverter
import com.github.xelamanster.beanconverter.parser.TypedIterableParser

abstract class PdfConverter[T: TypedIterableParser] extends DefaultBeanConverter[T, PdfSettings](PdfReader)
