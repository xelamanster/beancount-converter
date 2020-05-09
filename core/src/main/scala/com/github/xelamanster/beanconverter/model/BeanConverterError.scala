package com.github.xelamanster.beanconverter.model

sealed trait BeanConverterError

sealed trait BeanReaderError extends BeanConverterError
final case class FileReadError(e: Throwable) extends BeanReaderError
final case class FileParseError(messages: String*) extends BeanReaderError

final case class ConvertionError(messages: String*) extends BeanConverterError

final case class BeanCheckError(invalidTransactions: Seq[Transaction]) extends BeanConverterError {
  override def toString: String = {
    s"invalid: ${invalidTransactions.size} \n" + invalidTransactions.mkString(System.lineSeparator())
  }
}
