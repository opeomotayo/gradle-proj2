package com.nisum.myteam.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class MyTeamDateUtils {


    public static SimpleDateFormat getRadableDate() {
        return new SimpleDateFormat("dd-MMM-yyyy");
    }
    public static Date getYesterdayDate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static LocalDate convertUtilDateToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }


    public static Date getDayLessThanDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();
        log.info("Day before Date is::" + yesterday);
        return yesterday;
    }

    public static Date getDayMoreThanDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, +1);
        Date yesterday = calendar.getTime();
        log.info("Day after Date is::" + yesterday);
        return yesterday;
    }

    public static String FormatTodaysDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    public static Date parseTodaysDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }

    }

    public static boolean isTodayHoliday(String holidays) {
        boolean isHoliday=false;
        if(StringUtils.isNotBlank(holidays))
        {
            String[] holidayList = holidays.split(",");
            String todayDate = FormatTodaysDate();
            return Arrays.stream(holidayList).anyMatch(holiday -> holiday.equalsIgnoreCase(todayDate));
        }
       return isHoliday;
    }

   /* public boolean isTodayHoliday() {
        Set<Date> dateList = new HashSet<Date>();
        Date todayDate = new Date();
        log.info("The holidays list from properties file::" + holidays);
        String[] holidayList = holidays.split(",");
        for (String holiday : holidayList)
            dateList.add(parseTodaysDate(holiday));
        log.info("The dateList::" + dateList);
        return dateList.contains(todayDate);
    } */

    public static void main(String[] args) {
        MyTeamDateUtils dateUtils = new MyTeamDateUtils();

        //System.out.println(dateUtils.isTodayHoliday());
        //System.out.println(dateUtils.isTodayHoliday());

    }

}
