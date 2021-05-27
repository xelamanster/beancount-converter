package com.github.xelamanster.beanconverter.model

import cats.Show
import java.time.LocalDate

enum Transaction(date: LocalDate, from: Account, to: Account, comment: String):
  case Transfer(
    date: LocalDate,
    from: Account,
    to: Account,
    amount: BigDecimal,
    currency: Currency,
    comment: String
  ) extends Transaction(date, from, to, comment)

  case Exchange(
    date: LocalDate,
    from: Account,
    fromAmount: BigDecimal,
    fromCurrency: Currency,
    to: Account,
    toAmount: BigDecimal,
    toCurrency: Currency,
    rate: BigDecimal,
    comment: String
  ) extends Transaction(date, from, to, comment)

object Transaction:

  given Show[Transaction] with
    def show(t: Transaction): String = t match
      case t: Transfer => summon[Show[Transfer]].show(t)
      case e: Exchange => summon[Show[Exchange]].show(e)

  given Show[Transfer] with
    def show(t: Transfer): String =
      s"""${t.date} * "${t.comment.replaceAll("\"", "'")}"
         |  ${t.from} ${t.amount} ${t.currency}
         |  ${t.to} ${t.amount * -1} ${t.currency}""".stripMargin

  given Show[Exchange] with
    def show(e: Exchange): String =
      s"""${e.date} * "${e.comment.replaceAll("\"", "'")}"
         |  ${e.from} ${e.fromAmount} ${e.fromCurrency}
         |  ${e.to} ${e.toAmount * -1} ${e.toCurrency} @ ${e.rate}""".stripMargin
