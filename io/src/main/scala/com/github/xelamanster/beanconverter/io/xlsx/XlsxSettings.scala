package com.github.xelamanster.beanconverter.io.xlsx

import com.github.xelamanster.beanconverter.{Coordinate, FileSettings}

object XlsxSettings {

  def apply(fileName: String, worksheetId: Int, topLeft: Coordinate, bottomRight: Coordinate): XlsxSettings =
    XlsxSettings(
      fileName,
      worksheetId,
      topLeft,
      Coordinate(bottomRight.x, topLeft.y),
      Coordinate(topLeft.x, bottomRight.y),
      bottomRight
    )
}

final case class XlsxSettings(
    fileName: String,
    worksheetId: Int,
    topLeft: Coordinate,
    topRight: Coordinate,
    bottomLeft: Coordinate,
    bottomRight: Coordinate
) extends FileSettings
