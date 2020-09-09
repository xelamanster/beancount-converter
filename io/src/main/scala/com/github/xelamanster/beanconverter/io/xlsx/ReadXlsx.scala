package com.github.xelamanster.beanconverter.io.xlsx

import java.io.File

import com.github.xelamanster.beanconverter.BeanConverter.ReadFileRow
import com.github.xelamanster.beanconverter.{BeanReaderError, FileReadError}
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy
import org.apache.poi.ss.usermodel.{Row, WorkbookFactory}
import zio.IO

import scala.util.Using
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType

object ReadXlsx extends ReadFileRow[XlsxSettings] {

  override def apply(settings: XlsxSettings): IO[BeanReaderError, List[List[String]]] = {

    def cellToString(cell: Cell): String = cell.getCellType() match {
      case CellType.BLANK => ""
      case CellType.BOOLEAN => cell.getBooleanCellValue().toString()
      case CellType.NUMERIC => cell.getNumericCellValue().toString()
      case CellType.STRING => cell.getStringCellValue()
      case _ => ???
    }

    def cells(row: Row) = {
      (settings.topLeft.x - 1 until settings.topRight.x)
        .map(row.getCell(_, MissingCellPolicy.CREATE_NULL_AS_BLANK))
        .map(cellToString)
        .toList
    }

    readFile(settings).map(_.map(cells))
  }

  private def readFile(settings: XlsxSettings) =
    IO.fromTry(
      Using(WorkbookFactory.create(new File(settings.fileName))) { wb =>
        val sheet = wb.getSheetAt(settings.worksheetId)

        (settings.topLeft.y - 1 until settings.bottomLeft.y)
          .map(sheet.getRow)
          .toList
      }
    ).mapError(FileReadError.apply)
}
