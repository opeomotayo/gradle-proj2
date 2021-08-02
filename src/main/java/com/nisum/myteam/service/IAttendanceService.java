package com.nisum.myteam.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.AttendenceData;
import com.nisum.myteam.model.dao.EmpLoginData;

public interface IAttendanceService {

	List<AttendenceData> getAttendanciesReport(String reportDate, String shift) throws MyTeamException, SQLException;

	List generatePdfReport(long id, String fromDate, String toDate, String fromTime, String toTime)
			throws MyTeamException;

	List<EmpLoginData> employeeLoginReportBasedOnDateTime(long id, String fromDate, String toDate, String fromTime,
			String toTime) throws MyTeamException, ParseException;

	
	
	Boolean fetchEmployeesData(String perticularDate, boolean resynchFlag) throws MyTeamException;

	List<EmpLoginData> employeeLoginsBasedOnDate(long id, String fromDate, String toDate) throws MyTeamException;

	List generatePdfReport(long id, String fromDate, String toDate) throws MyTeamException;

}
