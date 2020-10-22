package com.github.xelamanster.beanconverter

import com.github.xelamanster.beanconverter.model.Account
import com.github.xelamanster.beanconverter.model.Transaction
import com.github.xelamanster.beanconverter.model.Row
import com.github.xelamanster.beanconverter.model.operations.Operation

class MergeSettings[T <: Row, S <: FileSettings, T2 <: Row, S2 <: FileSettings](
    val readSettings: ReadSettings[T, S],
    val readSettingsFallback: ReadSettings[T2, S2],
    val exportSettings: WriteSettings,
    val replaceCheck: (Transaction, Transaction) => Boolean
)

class Settings[T <: Row, S <: FileSettings](
    val readSettings: ReadSettings[T, S],
    val writeSettings: WriteSettings
)

final case class ReadSettings[T <: Row, S <: FileSettings](
    filesSettings: Seq[S],
    contentSettings: ContentSettings
)

trait FileSettings {
  def fileName: String
}

final case class ContentSettings(
    sourceAccount: Account,
    targetMapping: Map[Operation, Seq[String]],
    miscMapping: Map[Operation, Seq[String]]
)

final case class Coordinate(x: Int, y: Int)

final case class WriteSettings(filename: String)
