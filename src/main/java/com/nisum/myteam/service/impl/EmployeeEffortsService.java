package com.nisum.myteam.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.nisum.myteam.model.dao.Account;
import com.nisum.myteam.model.dao.Project;
import com.nisum.myteam.model.dao.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nisum.myteam.configuration.DbConnection;
import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.EmployeeEfforts;
import com.nisum.myteam.service.IEmployeeEffortsService;
import com.nisum.myteam.utils.MyTeamLogger;
import com.nisum.myteam.utils.MyTeamUtils;

@Component
@Transactional
public class EmployeeEffortsService implements IEmployeeEffortsService {
	
	@Autowired
	DbConnection dbConnection;

	@Autowired
	ResourceService resourceService;

	@Autowired
	ProjectService projectService;

	@Autowired
	AccountService accountService;

	@Autowired
	EmployeeService employeeService;


	@Override
	public List<EmployeeEfforts> getEmployeeEffortsReport(String fromDate, String toDate)
			throws MyTeamException, SQLException {
		String query = null;
		query = MyTeamUtils.EMPLOYEE_EFFORTS_QUERY + "'" + fromDate.replace("-", "/") + "'"
				+ MyTeamUtils.EMPLOYEE_EFFORTS_QUERY1 + "'" + toDate.replace("-", "/") + "'"
				+ MyTeamUtils.EMPLOYEE_EFFORTS_QUERY2;		
		List<EmployeeEfforts> listOfEmpEffortsData = getEmployeeEfforts(query, fromDate, toDate);
		
		return listOfEmpEffortsData;
	}
	
	private List<EmployeeEfforts> getEmployeeEfforts(String query, String fromDate, String toDate) {


		List<EmployeeEfforts> listOfEmpEffortsData = new ArrayList<>();
		try (Connection connection = dbConnection.getDBConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query.toString())) {
			final Date from = new SimpleDateFormat("yyyy-mm-dd").parse(fromDate);
			final Date to = new SimpleDateFormat("yyyy-mm-dd").parse(toDate);
			while (resultSet.next()) {
				EmployeeEfforts employeeEfforts = new EmployeeEfforts();
				employeeEfforts.setEmployeeId(resultSet.getString("EmployeeCode"));
				employeeEfforts.setEmployeeName(resultSet.getString("FirstName"));
				employeeEfforts.setTotalHoursSpentInWeek(resultSet.getString("TotalHoursInWeek") + ':' + resultSet.getString("TotalMinutesInWeek"));
				Resource resource = resourceService.getCurrentAllocation(employeeEfforts.getEmployeeId());
				if (null != resource) {
					Project project = projectService.getProjectByProjectId(resource.getProjectId());
					Account account = accountService.getAccountById(project.getAccountId());
					employeeEfforts.setProjectName(project.getProjectName());
					employeeEfforts.setAccountName(account.getAccountName());
					employeeEfforts.setFunctionalOrg(employeeService.getEmployeeById(employeeEfforts.getEmployeeId()).getFunctionalGroup());
				}
				listOfEmpEffortsData.add(employeeEfforts);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MyTeamLogger.getInstance().error(e.getMessage());
		}
		
		return listOfEmpEffortsData;
	}

}


