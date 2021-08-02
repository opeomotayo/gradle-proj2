package com.nisum.myteam.fileexport;

import com.nisum.myteam.model.dao.MyStatus;
import com.nisum.myteam.utils.MyStatusFileUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

public class GenerateStatusExcelReport {
  public static ByteArrayInputStream generateExcelStatusReport(List<MyStatus> statusList) throws IOException {

    try (Workbook workbook = new XSSFWorkbook()) {

      Sheet sheet = workbook.createSheet("Status Report");
      sheet.setDefaultColumnWidth(30);

      // create style for header cells
      CellStyle style = workbook.createCellStyle();
      Font font = workbook.createFont();
      font.setFontName("Arial");
      style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
      style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      font.setBold(true);
      style.setFont(font);

      // create header row
      Row header = sheet.createRow(0);
      header.createCell(0).setCellValue("Task Date");
      header.getCell(0).setCellStyle(style);
      header.createCell(1).setCellValue("Task Type");
      header.getCell(1).setCellStyle(style);
      header.createCell(2).setCellValue("Ticket Number");
      header.getCell(2).setCellStyle(style);
      header.createCell(3).setCellValue("Story Points");
      header.getCell(3).setCellStyle(style);
      header.createCell(4).setCellValue("Planned startDate");
      header.getCell(4).setCellStyle(style);
      header.createCell(5).setCellValue("Planned endDate");
      header.getCell(5).setCellStyle(style);
      header.createCell(6).setCellValue("Actual startDate");
      header.getCell(6).setCellStyle(style);
      header.createCell(7).setCellValue("Actual endDate");
      header.getCell(7).setCellStyle(style);
      header.createCell(8).setCellValue("Hours spent");
      header.getCell(8).setCellStyle(style);
      header.createCell(9).setCellValue("Priority");
      header.getCell(9).setCellStyle(style);
      header.createCell(10).setCellValue("Status");
      header.getCell(10).setCellStyle(style);
      header.createCell(11).setCellValue("Task Details");
      header.getCell(11).setCellStyle(style);
      header.createCell(12).setCellValue("Comments");
      header.getCell(12).setCellStyle(style);

      int rowCount = 1;


      for (MyStatus status : statusList) {
        Row statusRow = sheet.createRow(rowCount++);
        Cell taksDateCell = statusRow.createCell(0);
        taksDateCell.setCellValue(status.getTaskDate());
        taksDateCell.setCellStyle(MyStatusFileUtil.cellStyleDate(workbook));

        statusRow.createCell(1).setCellValue(status.getTaskType());
        statusRow.createCell(2).setCellValue(status.getTicketNumber());
        statusRow.createCell(3).setCellValue(status.getStoryPoints());

        Cell planedTaskStartDateCell = statusRow.createCell(4);
        planedTaskStartDateCell.setCellValue(status.getPlanedStartDate());
        planedTaskStartDateCell.setCellStyle(MyStatusFileUtil.cellStyleDate(workbook));

        Cell planedTaskEndDateCell = statusRow.createCell(5);
        planedTaskEndDateCell.setCellValue(status.getPlanedEndDate());
        planedTaskEndDateCell.setCellStyle(MyStatusFileUtil.cellStyleDate(workbook));

        Cell actualStartDateCell = statusRow.createCell(6);
        actualStartDateCell.setCellValue(status.getActualEndDate());
        actualStartDateCell.setCellStyle(MyStatusFileUtil.cellStyleDate(workbook));

        Cell actualEndDateCell = statusRow.createCell(7);
        actualEndDateCell.setCellValue(status.getActualEndDate());
        actualEndDateCell.setCellStyle(MyStatusFileUtil.cellStyleDate(workbook));

        statusRow.createCell(8).setCellValue(status.getHoursSpent());
        statusRow.createCell(9).setCellValue(status.getPriority());
        statusRow.createCell(10).setCellValue(status.getStatus());
        statusRow.createCell(11).setCellValue(status.getTaskDetails());
        statusRow.createCell(12).setCellValue(status.getComments());
      }
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return new ByteArrayInputStream(outputStream.toByteArray());
    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }
  }
}
