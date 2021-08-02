package com.nisum.myteam.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommomUtil {
	   public static List<String> getAddedManagersList(List<String> fromDb, List<String> fromUser) {
	        List<String> addedManagers = new ArrayList<String>();
	        if (fromDb != null)
	            for (String managerFromUser : fromUser) {
	                if (!fromDb.contains(managerFromUser))
	                    addedManagers.add(managerFromUser);
	            }
	        return addedManagers;
	    }
	   
	public static List<String> getAddedManagersListForDM(List<Object> fromDb, List<Object> fromUser) {
		List<String> addedManagers = new ArrayList<String>();
		if (fromDb != null)
			for (Object managerFromUser : fromUser) {
				if (!fromDb.contains(managerFromUser.toString()))
					addedManagers.add(managerFromUser.toString());
			}
		return addedManagers;
	}
	   
	    
	    public static List<String> getDeletedManagersList(List<String> fromDb, List<String> fromUser) {
	        List<String> deletedManager = new ArrayList<String>();
	        if (fromDb != null)
	            for (String managerFromDb : fromDb) {
	                if (!fromUser.contains(managerFromDb))
	                    deletedManager.add(managerFromDb);
	            }
	        return deletedManager;
	    }

	    public static String appendZero(int val) { 
	    	return val<10?("0"+val):(val+"");  
	    } 
	    
	    public static String convertTimeFormat(String time) throws ParseException {
		     SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
		     SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
     	     Date  from = parseFormat.parse(time); 
			 return displayFormat.format(from); 
	    	
	    }
	    
	    public static String getNextDay(String fromDate,String fromTime,String toTime) throws ParseException {
	    	String timeSignFrom=fromTime.substring(6,8);
			String  timeSignTo=toTime.substring(6,8);
			String timeFrom= convertTimeFormat(fromTime);
		    String timeTo= convertTimeFormat(toTime);
		   //Getting Hours from Time             
		    String nextDate=fromDate;       
			int hoursFrom=Integer.parseInt(timeFrom.substring(0,2));
			int hoursTo=Integer.parseInt(timeTo.substring(0,2));
			if(hoursTo<=hoursFrom && timeSignFrom.equals(timeSignTo)
					|| hoursTo<=hoursFrom /*this condition is applicable if fromTime is PM & toTime is AM*/) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");        
				LocalDate nextDay = (LocalDate.parse(fromDate,formatter)).plusDays(1); 	     
				nextDate=nextDay.toString();               
			}  
	    	return nextDate;
	    }
}
