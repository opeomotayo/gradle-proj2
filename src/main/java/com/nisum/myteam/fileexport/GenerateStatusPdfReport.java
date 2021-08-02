package com.nisum.myteam.fileexport;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nisum.myteam.model.dao.MyStatus;
import com.nisum.myteam.utils.MyStatusFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.List;

public class GenerateStatusPdfReport {
  public static ByteArrayInputStream statusReport(List<MyStatus> statusResponse) {

    final Logger log = LoggerFactory.getLogger(GenerateStatusPdfReport.class);

    Document document = new Document();
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    try{

      PdfPTable table = new PdfPTable(13);
      table.setWidthPercentage(113);

      Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

      PdfPCell hcell;

      hcell = new PdfPCell(new Phrase("Task Date", headFont));
      hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(hcell);

      hcell = new PdfPCell(new Phrase("Task Type", headFont));
      hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(hcell);

      hcell = new PdfPCell(new Phrase("Ticket Number", headFont));
      hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(hcell);

      hcell = new PdfPCell(new Phrase("Story Points", headFont));
      hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(hcell);

      hcell = new PdfPCell(new Phrase("Planned startDate", headFont));
      hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
      table.addCell(hcell);

      hcell = new PdfPCell(new Phrase("Planned endDate", headFont));
      hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
      table.addCell(hcell);

      hcell = new PdfPCell(new Phrase("Actual startDate", headFont));
      hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
      table.addCell(hcell);

      hcell = new PdfPCell(new Phrase("Actual endDate", headFont));
      hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
      table.addCell(hcell);

      hcell = new PdfPCell(new Phrase("Hours spent", headFont));
      hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(hcell);

      hcell = new PdfPCell(new Phrase("Priority", headFont));
      hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
      table.addCell(hcell);

      hcell = new PdfPCell(new Phrase("Status", headFont));
      hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(hcell);

      hcell = new PdfPCell(new Phrase("Task Details", headFont));
      hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(hcell);

      hcell = new PdfPCell(new Phrase("Comments", headFont));
      hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(hcell);

      for(MyStatus status: statusResponse){
        PdfPCell cell;

        cell = new PdfPCell(new Phrase(MyStatusFileUtil.dateToStringFormatter(status.getTaskDate())));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(status.getTaskType()));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(String.valueOf(status.getTicketNumber())));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(String.valueOf(status.getStoryPoints())));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(MyStatusFileUtil.dateToStringFormatter((status.getPlanedStartDate()))));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(MyStatusFileUtil.dateToStringFormatter(status.getPlanedEndDate())));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(MyStatusFileUtil.dateToStringFormatter(status.getActualStartDate())));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(MyStatusFileUtil.dateToStringFormatter(status.getActualEndDate())));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(String.valueOf(status.getHoursSpent())));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(String.valueOf(status.getPriority())));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(status.getStatus()));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(String.valueOf(status.getTaskDetails())));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(String.valueOf(status.getComments())));
        table.addCell(cell);
      }
      PdfWriter.getInstance(document, out);
      document.open();
      document.add(table);
      document.close();
    }catch (DocumentException ex) {
      log.error("Error occurred", ex);
    }
    return new ByteArrayInputStream(out.toByteArray());
  }
}
