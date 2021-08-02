package com.nisum.myteam.utils;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Workbook;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyStatusFileUtil {
  public static  CellStyle cellStyleDate(Workbook workbook) {
    try{
      CellStyle cellStyle = workbook.createCellStyle();
      CreationHelper createHelper = workbook.getCreationHelper();
      short dateFormat = createHelper.createDataFormat().getFormat("yyyy-dd-MM");
      cellStyle.setDataFormat(dateFormat);
      return cellStyle;
    }catch(Exception e){
      throw e;
    }
  }

  public static String dateToStringFormatter(Date date){
    String strDate = "";
    try{
      if(date != null){
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        strDate = formatter.format(date);
        return strDate;
      }
    }
    catch(Exception e){
      throw e;
    }
    return strDate;
  }
}
