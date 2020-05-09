package com.github.xelamanster.beanconverter.model.operations

import com.github.xelamanster.beanconverter.model.Account
import com.github.xelamanster.beanconverter.model.Currency

sealed trait Operation

object Operation {
  final case class Transfer(target: Account) extends Operation
  final case class Exchange(target: Account, fromCurrency: Currency.Value, toCurrency: Currency.Value) extends Operation
  final case object Ignore extends Operation
}
