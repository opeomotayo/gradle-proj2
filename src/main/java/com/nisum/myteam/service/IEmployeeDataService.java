package com.nisum.myteam.service;

import java.util.List;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.EmpLoginData;

public interface IEmployeeDataService {
	List<EmpLoginData> fetchEmployeeLoginsBasedOnDates(long employeeId, String fromDate, String toDate) throws MyTeamException;
	List<EmpLoginData> fetchEmployeeLoginsBasedOnDatesTime(long employeeId, String fromDate, String nextDate,String toDate,String fromTime,String toTime) throws MyTeamException;
}
