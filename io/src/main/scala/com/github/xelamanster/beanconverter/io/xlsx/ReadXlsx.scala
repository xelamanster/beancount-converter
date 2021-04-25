package com.github.xelamanster.beanconverter.io.xlsx

import java.io.File

import com.github.xelamanster.beanconverter.BeanConverter.ReadFileRow
import com.github.xelamanster.beanconverter.{BeanReaderError, FileReadError}
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy
import org.apache.poi.ss.usermodel.{Row, WorkbookFactory}

import scala.util.Using
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType

object ReadXlsx extends ReadFileRow[XlsxSettings] {

  override def apply(settings: XlsxSettings): Either[BeanReaderError, List[List[String]]] = {

    def cellToString(cell: Cell): String = cell.getCellType() match {
      case CellType.BLANK => ""
      case CellType.BOOLEAN => cell.getBooleanCellValue().toString()
      case CellType.NUMERIC => cell.getNumericCellValue().toString()
      case CellType.STRING => cell.getStringCellValue()
      case _ => ???
    }

    def cells(row: Row) = {
      val (topLeftX, _) = settings.topLeft
      val (topRightX, _) = settings.topRight
      (topLeftX - 1 until topRightX)
        .map(row.getCell(_, MissingCellPolicy.CREATE_NULL_AS_BLANK))
        .map(cellToString)
        .toList
    }

    readFile(settings).map(_.map(cells))
  }

  private def readFile(settings: XlsxSettings) =
    Using(WorkbookFactory.create(new File(settings.fileName))) { wb =>
      val sheet = wb.getSheetAt(settings.worksheetId)
      val (_, topLeftY) = settings.topLeft
      val (_, topRightY) = settings.topRight
      (topLeftY - 1 until topRightY)
        .map(sheet.getRow)
        .toList
    }.toEither.left.map(FileReadError.apply)
}
