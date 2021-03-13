package com.github.xelamanster.beanconverter.model

import cats.Show
import cats.implicits._
import java.time.LocalDate

sealed trait Transaction {
  def date: LocalDate
  def from: Account
  def to: Account
  def comment: String
}

object Transaction {

  object implicits {

    implicit val showT: Show[Transfer] =
      t => s"""${t.date} * "${t.comment.replaceAll("\"", "'")}"
              |  ${t.from.name} ${t.amount} ${t.currency}
              |  ${t.to.name} ${t.amount * -1} ${t.currency}""".stripMargin

    implicit val showE: Show[Exchange] =
      e => s"""${e.date} * "${e.comment.replaceAll("\"", "'")}"
              |  ${e.from.name} ${e.fromAmount} ${e.fromCurrency}
              |  ${e.to.name} ${e.toAmount * -1} ${e.toCurrency} @ ${e.rate}""".stripMargin

    implicit val beanShow: Show[Transaction] = {
      case t: Transfer => t.show
      case e: Exchange => e.show
    }
  }

  case class Transfer(
      date: LocalDate,
      from: Account,
      to: Account,
      amount: BigDecimal,
      currency: Currency,
      comment: String
  ) extends Transaction

  case class Exchange(
      date: LocalDate,
      from: Account,
      fromAmount: BigDecimal,
      fromCurrency: Currency,
      to: Account,
      toAmount: BigDecimal,
      toCurrency: Currency,
      rate: BigDecimal,
      comment: String
  ) extends Transaction

}
