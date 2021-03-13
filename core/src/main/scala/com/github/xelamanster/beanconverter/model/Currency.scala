package com.github.xelamanster.beanconverter.model

enum Currency(code: Int, translations: Seq[String] = Seq.empty) {
  case UAH extends Currency(1, Seq("ГРН"))
  case EUR extends Currency(2)
  case CZK extends Currency(3)
  case CHF extends Currency(4)
  case USD extends Currency(5)
  case RUB extends Currency(6)
  case PLN extends Currency(7)
  case ISK extends Currency(8)
  case THB extends Currency(9)
  case SEK extends Currency(10)
}
