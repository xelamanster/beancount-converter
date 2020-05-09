package com.github.xelamanster.beanconverter.parser

sealed trait TypedParserError extends Exception {
  def message: String
}

final case class ParsingError(message: String) extends TypedParserError

final case class TypeError(message: String) extends TypedParserError
