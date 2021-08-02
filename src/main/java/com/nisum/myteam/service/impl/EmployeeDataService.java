package com.nisum.myteam.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nisum.myteam.configuration.DbConnection;
import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.EmpLoginData;
import com.nisum.myteam.service.IEmployeeDataService;
import com.nisum.myteam.utils.CommomUtil;
import com.nisum.myteam.utils.MyTeamLogger;
import com.nisum.myteam.utils.MyTeamUtils;

@Component
@Transactional
@Slf4j
public class EmployeeDataService implements IEmployeeDataService {

	@Autowired
	DbConnection dbConnection;

	public List<EmpLoginData> fetchEmployeeLoginsBasedOnDates(long employeeId, String fromDate, String toDate)
			throws MyTeamException {
		long start_ms = System.currentTimeMillis();
		String querys = null;
		if (employeeId == 0) {
			querys = MyTeamUtils.ABESENT_STATUS_QUERY + "'" + fromDate.replace("-", "/") + "'"
					+ MyTeamUtils.ABESENT_STATUS_QUERY1 + "'" + toDate.replace("-", "/") + "'"
					+ MyTeamUtils.ABESENT_STATUS_QUERY3;
		} else {
			querys = MyTeamUtils.ABESENT_STATUS_QUERY + "'" + fromDate.replace("-", "/") + "'"
					+ MyTeamUtils.ABESENT_STATUS_QUERY1 + "'" + toDate.replace("-", "/") + "'"
					+ MyTeamUtils.ABESENT_STATUS_QUERY2 + employeeId + MyTeamUtils.ABESENT_STATUS_QUERY3;
		}
		List<EmpLoginData> listOfEmpLoginData = executeQuery(querys, 1);
		MyTeamLogger.getInstance()
				.info(" Time Taken fecth Employee data based on Dates ::: " + (System.currentTimeMillis() - start_ms));
		return listOfEmpLoginData;
	}

	public List<EmpLoginData> fetchEmployeeLoginsBasedOnDatesTime(long employeeId, String fromDate, String nextDate,
			String toDate, String fromTime, String toTime) throws MyTeamException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate formatedFromDate = LocalDate.parse(fromDate, formatter);
		LocalDate formatedNextDate = LocalDate.parse(nextDate, formatter);
		LocalDate formatedToDate = LocalDate.parse(toDate, formatter);
		int differentBetweenDays = Period.between(formatedFromDate, formatedToDate).getDays();
		long start_ms = System.currentTimeMillis();
		String query = null;
		List<EmpLoginData> resultSetData = new ArrayList<>();
		List<EmpLoginData> listOfEmpLoginDetails = new ArrayList<>();
		for (int i = 0; i <= differentBetweenDays; i++) {
			String fromDateTime = fromDate + " " + fromTime;
			String toDateTime = nextDate + " " + toTime;
			if (i > 0) {
				fromDate = formatedFromDate.plusDays(1).toString();
				formatedFromDate = LocalDate.parse(fromDate);
				nextDate = formatedNextDate.plusDays(1).toString();
				formatedNextDate = LocalDate.parse(nextDate);
				fromDateTime = fromDate + " " + fromTime;
				toDateTime = nextDate + " " + toTime;
			}
			if (i == differentBetweenDays && formatedNextDate == LocalDate.now()) {
				toDateTime = nextDate + " " + "23:59";
			}
			query = MyTeamUtils.LOGIN_REPORT_BY_TIME + "'" + fromDateTime + "'" + MyTeamUtils.LOGIN_REPORT_BY_TIME2 + "'"
					+ toDateTime + "'" + MyTeamUtils.LOGIN_REPORT_BY_TIME3 + employeeId + MyTeamUtils.LOGIN_REPORT_BY_TIME4;
			resultSetData = executeQuery(query, 2);
			if (resultSetData.size() > 0) {
				listOfEmpLoginDetails.add(resultSetData.get(0));
			}
			MyTeamLogger.getInstance().info(
					" Time Taken fecth Employee data based on Dates ::: " + (System.currentTimeMillis() - start_ms));
		}
		return listOfEmpLoginDetails;
	}

	private List<EmpLoginData> executeQuery(String query, int flag) throws MyTeamException {
		long start_ms = System.currentTimeMillis();
		long totalseconds = 0;
		int countHours = 0;
		List<EmpLoginData> listOfEmpLoginData = new ArrayList<>();
		log.info("The Query going to execute::"+query);
		try (Connection connection = dbConnection.getDBConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query.toString())) {
			while (resultSet.next()) {
				EmpLoginData empLoginData = new EmpLoginData();
				empLoginData.setEmployeeId(resultSet.getString("EmployeeCode"));
				empLoginData.setEmployeeName(resultSet.getString("FirstName"));
				empLoginData.setDateOfLogin(resultSet.getDate("FirstLogin").toString());
				empLoginData.setFirstLogin(resultSet.getTime("FirstLogin").toString());
				empLoginData.setLastLogout(resultSet.getTime("LastLogin").toString());
				if (flag == 1) {
					Time from = resultSet.getTime("FirstLogin");
					Time to = resultSet.getTime("LastLogin");
					long milliseconds = to.getTime() - from.getTime();
					totalseconds = milliseconds / 1000;
				}
				if (flag == 2) {
					LocalDateTime loginTime = resultSet.getTimestamp("FirstLogin").toLocalDateTime();
					LocalDateTime logoutTime = resultSet.getTimestamp("LastLogin").toLocalDateTime();
					totalseconds = java.time.Duration.between(loginTime, logoutTime).getSeconds();
				}
				int hours = (int) (totalseconds / 3600);
				int minutes = (int) ((totalseconds % 3600) / 60);
				int seconds = (int) ((totalseconds % 3600) % 60);
				empLoginData.setTotalLoginTime(CommomUtil.appendZero(hours) + ":" + CommomUtil.appendZero(minutes) + ":"
						+ CommomUtil.appendZero(seconds)); 
				listOfEmpLoginData.add(empLoginData);
				if (flag == 1 && !listOfEmpLoginData.isEmpty()) {
					Date loginTime = MyTeamUtils.tdf.parse(empLoginData.getTotalLoginTime());
					countHours += loginTime.getTime(); 
					listOfEmpLoginData.get(0).setTotalAvgTime(MyTeamUtils.tdf.format(countHours / listOfEmpLoginData.size()));
				}
				MyTeamLogger.getInstance()
						.info(" Time Taken to execute the query to fecth Employee data based on Dates ::: "
								+ (System.currentTimeMillis() - start_ms));
			}
		} catch (Exception e) {
			e.printStackTrace();
			MyTeamLogger.getInstance().error(e.getMessage());
			throw new MyTeamException(e.getMessage());
		}
		return listOfEmpLoginData;
	}
}