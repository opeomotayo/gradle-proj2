package com.nisum.myteam.service;

import java.util.Date;
import java.util.List;

import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.dao.EmployeeSubStatus;
import com.nisum.myteam.model.vo.EmployeeSubStatusVO;

public interface ISubStatusService {
    public EmployeeSubStatus getLatestEmployeeSubStatus(String empId);

    public EmployeeSubStatus updateExistingEmplyeeSubStatus(String loginnEmployeeId,EmployeeSubStatus employeeSubStatusReq);

    public EmployeeSubStatus addEmployeeSubStatus(String loginnEmployeeId,EmployeeSubStatus employeeSubStatus);

    public EmployeeSubStatus getCurrentSubStatus(String employeeId);

    public EmployeeSubStatus endSubStatus(String loginnEmployeeId,EmployeeSubStatus subStatus);

	public List<EmployeeSubStatusVO> employeesBasedOnSubStatusForGivenDates(Date fromDate, Date toDate, String subStatus);
}
