package com.github.xelamanster.beanconverter

import com.github.xelamanster.beanconverter.model.operations.Operation
import com.github.xelamanster.beanconverter.model.{Account, BeanConverter}
import com.github.xelamanster.beanconverter.model.Transaction

class MergeSettings[T, S <: FileSettings, T2, S2 <: FileSettings](
    val readSettings: ReadSettings[T, S],
    val readSettingsFallback: ReadSettings[T2, S2],
    val exportSettings: ExportSettings,
    val replaceCheck: ReplaceCheck
)

class Settings[T, S <: FileSettings](
    val readSettings: ReadSettings[T, S],
    val exportSettings: ExportSettings
)

case class ReadSettings[T, S <: FileSettings](
    val filesSettings: Seq[S],
    val contentSettings: ContentSettings,
    val converter: BeanConverter[T, S]
)

trait FileSettings {
  def fileName: String
}

final case class CsvSettings(fileName: String) extends FileSettings

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
