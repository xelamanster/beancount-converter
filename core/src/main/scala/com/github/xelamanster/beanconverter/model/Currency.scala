package com.github.xelamanster.beanconverter.model

object Currency extends Enumeration {

  protected case class Val(code: Int, translations: Seq[String] = Seq.empty) extends super.Val

  import scala.language.implicitConversions
  implicit def valueToCurrency(x: Value): Val = x.asInstanceOf[Val]

  val UAH = Val(1, Seq("ГРН"))
  val EUR = Val(2)
  val CZK = Val(3)
  val CHF = Val(4)
  val USD = Val(5)
  val RUB = Val(6)
  val PLN = Val(7)
  val ISK = Val(8)
  val THB = Val(9)
  val SEK = Val(10)
}
