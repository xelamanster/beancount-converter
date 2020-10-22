package com.github.xelamanster.beanconverter.model

object Currency extends Enumeration {

  protected case class Currency(code: Int, translations: Seq[String] = Seq.empty) extends super.Val

  import scala.language.implicitConversions
  implicit def valueToCurrency(x: Value): Val = x.asInstanceOf[Val]

  val UAH = Currency(1, Seq("ГРН"))
  val EUR = Currency(2)
  val CZK = Currency(3)
  val CHF = Currency(4)
  val USD = Currency(5)
  val RUB = Currency(6)
  val PLN = Currency(7)
  val ISK = Currency(8)
  val THB = Currency(9)
  val SEK = Currency(10)
}
