package com.github.xelamanster.beanconverter.io

import com.github.xelamanster.beanconverter.model.Currency
import com.github.xelamanster.beanconverter.parser.Parser
import com.github.xelamanster.beanconverter.parser.DecodeError
import com.github.xelamanster.beanconverter.parser.FieldDecoder
import com.github.xelamanster.beanconverter.parser.RawField

import scala.util.Try

package object parser {

  given FieldDecoder[Currency] with
    def decode(raw: RawField): Either[DecodeError, Currency] =
      Try(Currency.valueOf(raw)).toEither.left.map(_ => DecodeError(s"error while parsing [$raw]")) 

}
