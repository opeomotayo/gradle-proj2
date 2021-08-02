package com.nisum.myteam.service;

import java.util.Date;
import java.util.List;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.Reports;
import com.nisum.myteam.model.dao.Project;
import com.nisum.myteam.model.vo.ReportVo;

public interface IReportService {

    public ReportVo getBarChartReport(String byType, Date onDate) throws MyTeamException;

	public List<Reports> getEmployeeDetailsByFGAndBillability(String fGroup, String billableStatus,Date onDate) throws MyTeamException;

	public List<Reports> getEmployeeDetailsByAccountBillability(String account, String billabilityStatus,Date onDate)throws MyTeamException;

	public Project getProjectById(String employeeId);

	public List<Reports> getEmployeeDetailsByFGAccountAndBillability(String fGroup, String billableStatus, String account,Date onDate) throws MyTeamException;

	public ReportVo getPieChartReport(String byType, Date onDate) throws MyTeamException;

	public List<Reports> getEmployeeDetailsByBillabilityType(String billabilityType, Date onDate) throws MyTeamException;
}
 