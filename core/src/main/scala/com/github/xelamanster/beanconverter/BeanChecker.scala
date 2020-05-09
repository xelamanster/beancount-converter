package com.github.xelamanster.beanconverter

import cats.data.ValidatedNec
import cats.implicits._
import com.github.xelamanster.beanconverter.model.{BeanCheckError, BeanConverterError, Transaction}

object BeanChecker {

  def check(
      beans: Seq[Transaction]
  ): Either[BeanConverterError, Seq[Transaction]] =
    beans.toVector
      .traverse(check)
      .toEither
      .leftMap(e => BeanCheckError(e.toList.flatMap(_.invalidTransactions)))

  private def check(bean: Transaction): ValidatedNec[BeanCheckError, Transaction] = {
    // Either.cond()
    bean.valid
  }

}
