package com.nisum.myteam.service;

import com.nisum.myteam.model.dao.MyStatus;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface MyStatusService {
  public MyStatus saveStatusDetails(MyStatus status);
  public List<MyStatus> findTodayStatus(String empId);
  public List<MyStatus> findTaskDataByTaskDate(String empId, LocalDate date);
  public List<MyStatus> getStatusDetailsByEmpId(String empId);
  public List<MyStatus> getByDateRange(Date fromDate, Date toDate, String empId);
  public List<MyStatus> getEmployeeStatusDetails();
  public MyStatus updateStatusDetails(MyStatus status);
  public void deleteEmployeeStatusDetails(Integer id);
  public List<MyStatus> getDefaultStatusDetails(String empId);
}
