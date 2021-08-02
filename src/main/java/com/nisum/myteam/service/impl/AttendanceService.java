package com.nisum.myteam.service.impl;

import com.nisum.myteam.configuration.DbConnection;
import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.AttendenceData;
import com.nisum.myteam.model.dao.EmpLoginData;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.service.IAttendanceService;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.utils.CommomUtil;
import com.nisum.myteam.utils.MyTeamLogger;
import com.nisum.myteam.utils.MyTeamUtils;
import com.nisum.myteam.service.PdfReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AttendanceService implements IAttendanceService {

    @Autowired
    private EmployeeDataService employeeDataBaseService;

    @Autowired
    DbConnection dbConnection;

    @Autowired
    private PdfReportGenerator pdfReportGenerator;

    @Autowired
    private IEmployeeService employeeService;

    @Override
    public List<AttendenceData> getAttendanciesReport(String reportDate, String shift) throws MyTeamException {
        long start_ms = System.currentTimeMillis();
        String reportDateInReqDateFormat = reportDate.replace("-", "/");
        List<AttendenceData> listOfEmployees = getEmpsAttendenceByShiftWise(reportDateInReqDateFormat, shift);
        MyTeamLogger.getInstance().info("Time Taken for " + (System.currentTimeMillis() - start_ms));
        return listOfEmployees;
    }

    @Override
    public List<EmpLoginData> employeeLoginReportBasedOnDateTime(long id, String fromDate, String toDate, String fromTime, String toTime) throws MyTeamException, ParseException {
        String timeFrom = CommomUtil.convertTimeFormat(fromTime);
        String timeTo = CommomUtil.convertTimeFormat(toTime);
        String nextDayDate = CommomUtil.getNextDay(fromDate, fromTime, toTime);
        return employeeDataBaseService.fetchEmployeeLoginsBasedOnDatesTime(id, fromDate, nextDayDate, toDate, timeFrom, timeTo);
    }

    @Override
    public List generatePdfReport(long id, String fromDate, String toDate, String fromTime, String toTime)
            throws MyTeamException {
        return pdfReportGenerator.generateEmployeeReport(id, fromDate, toDate);
    }


    private List<AttendenceData> getEmpsAttendenceByShiftWise(String reportDate, String shift) throws MyTeamException {
        List<AttendenceData> listOfEmployees = new ArrayList<AttendenceData>();
        List<Employee> list = findEmpIdsByShiftWise(shift);
        if (list.size() > 0) {
            List<String> empIdList = list.stream().map(Employee::getEmployeeId).collect(Collectors.toList());
            if (null != empIdList && empIdList.size() > MyTeamUtils.INT_ZERO) {
                String query = buildSqlQuery(reportDate, empIdList.toString().substring(1, empIdList.toString().length() - 1), MyTeamUtils.PRESENT);
                listOfEmployees.addAll(getAttendenceData(query, MyTeamUtils.PRESENT));

                List<String> presentList = listOfEmployees.stream().map(AttendenceData::getEmployeeId).collect(Collectors.toList());
                empIdList.removeAll(presentList);
                if (empIdList.size() > MyTeamUtils.INT_ZERO) {
                    query = buildSqlQuery(reportDate, empIdList.toString().substring(1, empIdList.toString().length() - 1), MyTeamUtils.ABSENT);
                    listOfEmployees.addAll(getAttendenceData(query, MyTeamUtils.ABSENT));
                }
            }
        }
        return listOfEmployees;
    }

    private List<Employee> findEmpIdsByShiftWise(String shift) {
        List<Employee> list = null;

        if (MyTeamUtils.ALL.equalsIgnoreCase(shift)) {
            list = employeeService.getEmployeesByEmpStatusAndShift("Active", MyTeamUtils.SHIFT);
        } else {
            list = employeeService.getEmployeesByEmpStatusAndShift("Active", shift);
        }
        return list;

    }

    private String buildSqlQuery(String reportDate, String empIdsStr, String type) {
        String query = null;
        if (MyTeamUtils.PRESENT.equals(type)) {
            query = MyTeamUtils.PRESENT_QUERY.replace("<REPORTDATE>", reportDate)
                    .replace("<EMPIDS>", empIdsStr);
        } else {
            query = MyTeamUtils.ABSENT_QUERY.replace("<ABSENTLIST>", empIdsStr);
        }
        return query;
    }

    private List<AttendenceData> getAttendenceData(String query, String type) throws MyTeamException {
        List<AttendenceData> listOfEmployees = null;
        try (Connection connection = dbConnection.getDBConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            AttendenceData attendData = null;
            listOfEmployees = new ArrayList<AttendenceData>();
            while (resultSet.next()) {
                attendData = new AttendenceData();
                attendData.setEmployeeId(resultSet.getString("EmployeeCode"));
                attendData.setEmployeeName(resultSet.getString("FirstName"));
                attendData.setEmailId(resultSet.getString("EmailId"));
                attendData.setPresent(type);
                listOfEmployees.add(attendData);
                attendData = null;
            }
        } catch (Exception e) {
            MyTeamLogger.getInstance().error("Exception occured due to : ", e);
            throw new MyTeamException(e.getMessage());
        }
        return listOfEmployees;
    }


    // @Override
    public Boolean fetchEmployeesData(String perticularDate, boolean resynchFlag) throws MyTeamException {
        return true;
    }

    // @Override
    public List<EmpLoginData> employeeLoginsBasedOnDate(long id, String fromDate, String toDate)
            throws MyTeamException {
        return employeeDataBaseService.fetchEmployeeLoginsBasedOnDates(id, fromDate, toDate);
    }

    @Override
    public List generatePdfReport(long id, String fromDate, String toDate) throws MyTeamException {
        return pdfReportGenerator.generateEmployeeReport(id, fromDate, toDate);
    }


}


