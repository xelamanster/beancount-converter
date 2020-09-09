package com.github.xelamanster.beanconverter

import com.github.xelamanster.beanconverter.BeanConverter
import com.github.xelamanster.beanconverter.model.Account
import com.github.xelamanster.beanconverter.model.Transaction
import com.github.xelamanster.beanconverter.model.Row
import com.github.xelamanster.beanconverter.model.operations.Operation

class MergeSettings[T <: Row, S <: FileSettings, T2 <: Row, S2 <: FileSettings](
    val readSettings: ReadSettings[T, S],
    val readSettingsFallback: ReadSettings[T2, S2],
    val exportSettings: ExportSettings,
    val replaceCheck: ReplaceCheck
)

class Settings[T <: Row, S <: FileSettings](
    val readSettings: ReadSettings[T, S],
    val exportSettings: ExportSettings
)

final case class ReadSettings[T <: Row, S <: FileSettings](
    val filesSettings: Seq[S],
    val contentSettings: ContentSettings,
    val converter: BeanConverter[T, S]
)

trait FileSettings {
  def fileName: String
}

case class ContentSettings(
    sourceAccount: Account,
    targetMapping: Map[Operation, Seq[String]],
    miscMapping: Map[Operation, Seq[String]]
)

case class Coordinate(x: Int, y: Int)

case class ExportSettings(targetFilename: String)

trait ReplaceCheck {
  def shouldReplace(original: Transaction)(other: Transaction): Boolean
}
