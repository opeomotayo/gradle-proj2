package com.nisum.myteam.service;

import java.sql.SQLException;
import java.util.List;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.EmployeeEfforts;

public interface IEmployeeEffortsService {

	List<EmployeeEfforts> getEmployeeEffortsReport(String fromDate, String toDate) throws MyTeamException, SQLException;
}
