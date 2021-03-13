package com.github.xelamanster.beanconverter

import com.github.xelamanster.beanconverter.model._

sealed trait BeanConverterError extends Throwable

sealed trait BeanReaderError extends BeanConverterError
final case class FileReadError(e: Throwable) extends BeanReaderError
final case class FileParseError(messages: String*) extends BeanReaderError

final case class ConvertionError(messages: String*) extends BeanConverterError {
  override def toString(): String =
    s"""|Found ${messages.size} problems:
        |${messages.mkString(System.lineSeparator())}
        |""".stripMargin

}

final case class BeanCheckError(invalidTransactions: Seq[Transaction]) extends BeanConverterError {
  override def toString: String = {
    s"invalid: ${invalidTransactions.size} \n" + invalidTransactions.mkString(System.lineSeparator())
  }
}
