package com.nisum.myteam.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nisum.myteam.model.dao.MasterData;
import com.nisum.myteam.service.IMasterDataService;
import com.nisum.myteam.service.impl.OrgLocationService;

@Service
@Slf4j
public class DataValidations {
	
	@Autowired
	 IMasterDataService masterDataService;
	
	@Autowired
	 OrgLocationService orgLocationService;
	
	public static boolean validateNumber(String number) {
		boolean flag = false;
		number = number.trim();
		if(!MyTeamUtils.EMPTY_STRING.equals(number)) {
			flag = number.matches("^\\d+$") && isNumberInRange(number);
		}
		return flag;
	}
	
	public static boolean isNumberInRange(String number) {
		long empId = Long.parseLong(number);
 		return (empId >= MyTeamUtils.EMPID_START && empId<= MyTeamUtils.EMPID_END);
	}
		
	 public static boolean validateName(String name) {
    	boolean flag = false;
    	name = name.trim();
    	if(!MyTeamUtils.EMPTY_STRING.equals(name)) {
			String regx =  "^[\\p{L} ]+$";
		    Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
		    Matcher matcher = pattern.matcher(name);
		    flag = matcher.find();
		    log.info("flag:"+flag);
    	}
		return flag;
	}
	 
				
	public  static boolean isValidGender(String gender) {
		boolean flag =  false;
		gender = gender.trim();
		if( !MyTeamUtils.EMPTY_STRING.equals(gender)) {
			flag = gender.equalsIgnoreCase(MyTeamUtils.MALE) || gender.equalsIgnoreCase(MyTeamUtils.FEMALE) || gender.equalsIgnoreCase(MyTeamUtils.M) || gender.equalsIgnoreCase(MyTeamUtils.F);
		}
		return flag;
	}
			
	public static boolean isValidEmail(String email) {
		boolean flag = false;
		email =  email.trim();
		if( !MyTeamUtils.EMPTY_STRING.equals(email) && email.endsWith("@nisum.com")) {
			String regx =  "^[\\p{L}]+$";
			email = email.replace("@nisum.com", "");
			Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
		    Matcher matcher = pattern.matcher(email);
		    flag = matcher.find();
		}
		return flag;
	}
			
	public  static boolean isYesOrNo(String value) {
		boolean flag = false;
		value = value.trim();
		if(!MyTeamUtils.EMPTY_STRING.equals(value)) {
			flag = value.equalsIgnoreCase(MyTeamUtils.YES) || value.equalsIgnoreCase(MyTeamUtils.STRING_Y) || value.equalsIgnoreCase(MyTeamUtils.NO) || value.equalsIgnoreCase(MyTeamUtils.STRING_N);
		}
		return flag;
	}
		
	public   boolean isValidFunctionalGroup(String functionalGroup) {
		boolean flag = false;
		functionalGroup = functionalGroup.trim();
		if(!MyTeamUtils.EMPTY_STRING.equals(functionalGroup)) {
			List<MasterData> fsData = masterDataService.findByMasterDataTypeAndMasterDataNameAndActiveStatus(MyTeamUtils.MASTERDATA_FG, functionalGroup, true);
			log.info("FunctionalGroup Data::"+fsData);
			if(fsData!=null)
			{
				flag = fsData.size() > MyTeamUtils.INT_ZERO;
				log.info("flag value::"+flag);
			}

		}
		return flag;
	}
		
	public   boolean isValidDesignation(String designation) {
		boolean flag = false;
		designation = designation.trim();
		if( !MyTeamUtils.EMPTY_STRING.equals(designation)) {
			List<MasterData> designationData = masterDataService.findByMasterDataTypeAndMasterDataNameAndActiveStatus(MyTeamUtils.MASTERDATA_DESIGNATION, designation, true);
			flag = designationData.size() > MyTeamUtils.INT_ZERO;
		}
		return flag;
	}
		
		
	public   boolean isValidEmploymentType(String employmentType) {
		boolean flag = false;
		employmentType = employmentType.trim();
		if(MyTeamUtils.EMPTY_STRING.equals(employmentType)){
			flag = true;
		}else if( !MyTeamUtils.EMPTY_STRING.equals(employmentType)) {
			List<MasterData> empTypeData = masterDataService.findByMasterDataTypeAndMasterDataNameAndActiveStatus(MyTeamUtils.MASTERDATAD_EMLOYMENT_TYPE, employmentType, true);
			flag = empTypeData.size() > MyTeamUtils.INT_ZERO ;
		}
		return flag;
	}
		
	public   boolean isValidRole(String role) {
		boolean flag = false;
		role =  role.trim();
		if( MyTeamUtils.EMPTY_STRING.equals(role)){
			flag = true;
		}else if(!MyTeamUtils.EMPTY_STRING.equals(role)) {
			List<MasterData> roleData = masterDataService.findByMasterDataTypeAndMasterDataNameAndActiveStatus(MyTeamUtils.MASTERDATA_ROLES, role, true);
			flag = roleData.size() > MyTeamUtils.INT_ZERO;
		}
		return flag;
	}
		
	public  static boolean isFutureDate(Date date) {
		boolean flag = false;
		if(null != date) {
			flag = date.after(new Date()) ;
		}
		return flag;
	}
	
	public  static boolean isValidDate(Date date) {
		return null != date;
	}
	
	public  static boolean isAgeGreaterThanTwenty(Date dob, Date doj) {
		boolean flag = false;
		if (dob == null) {
			flag = true;
		} else if(null != doj) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(doj);
			int dojYear =  cal.get(Calendar.YEAR);
			cal.setTime(dob);
			flag = dojYear  - cal.get(Calendar.YEAR) > MyTeamUtils.INT_TWENTY;
		}
		return flag;
	}
	
	
	public   boolean isValidWorkLocation(String workLocation) {
		boolean flag = false;
		workLocation = workLocation.trim();
		if(!MyTeamUtils.EMPTY_STRING.equals(workLocation)) {
			flag= orgLocationService.findByLocationAndActiveStatus(workLocation, true). size() > MyTeamUtils.INT_ZERO ;
		}
		return flag;
	}
	
	public static boolean isActive(String active) {
		boolean flag = false;
		active = active.trim();
		if(null != active && MyTeamUtils.EMPTY_STRING.equals(active)) {
			flag =  MyTeamUtils.ACTIVE.equalsIgnoreCase(active);
		}
		return flag;
	}
}
