package com.github.xelamanster.beanconverter.io.xlsx

import com.github.xelamanster.beanconverter.{Coordinate, FileSettings}

object XlsxSettings {

  def apply(fileName: String, topLeft: Coordinate, bottomRight: Coordinate): XlsxSettings =
    apply(fileName, 0, topLeft, bottomRight)

  def apply(fileName: String, worksheetId: Int, topLeft: Coordinate, bottomRight: Coordinate): XlsxSettings = {
    val (bottomRightX, bottomRightY) = bottomRight
    val (topLeftX, topLeftY) = topLeft
    XlsxSettings(
      fileName,
      worksheetId,
      topLeft,
      (bottomRightX, topLeftY),
      (topLeftX, bottomRightY),
      bottomRight
    )
  }
}

final case class XlsxSettings(
    fileName: String,
    worksheetId: Int,
    topLeft: Coordinate,
    topRight: Coordinate,
    bottomLeft: Coordinate,
    bottomRight: Coordinate
) extends FileSettings
