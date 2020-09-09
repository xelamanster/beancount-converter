package com.github.xelamanster.beanconverter.io

import com.github.xelamanster.beanconverter.model.Currency
import com.github.xelamanster.beanconverter.parser.Parser

import scala.util.Try

package object parser {

  object implicits {

    implicit val currency: Parser[Currency.Value] =
      rawCurrency => Try(Currency.withName(rawCurrency)).toOption
  }

}
