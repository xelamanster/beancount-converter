package com.github.xelamanster.beanconverter.model.operations

import com.github.xelamanster.beanconverter.model.Account
import com.github.xelamanster.beanconverter.model.Currency

enum Operation {
  case Transfer(target: Account)
  case Exchange(target: Account, fromCurrency: Currency, toCurrency: Currency)
  case Ignore
}
