package com.nisum.myteam.controller;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.AttendenceData;
import com.nisum.myteam.model.dao.EmpLoginData;
import com.nisum.myteam.service.IAttendanceService;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

	@Autowired
	private IAttendanceService attendanceService;

	@RequestMapping(value = "employeeLoginsBasedOnDate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmpLoginData>> employeeLoginsBasedOnDate(@RequestParam("empId") long id,
			@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate) throws MyTeamException {
		List<EmpLoginData> message = new ArrayList<>();
		try {
			message = attendanceService.employeeLoginsBasedOnDate(id, fromDate, toDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	@RequestMapping(value = "generatePdfReport/{id}/{fromDate}/{toDate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List> generatePdfReport(@PathVariable("id") long id,
			@PathVariable("fromDate") String fromDate, @PathVariable("toDate") String toDate) throws MyTeamException {
		List result = attendanceService.generatePdfReport(id, fromDate, toDate);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "generatePdfReport/{id}/{fromDate}/{toDate}/{fromTime}/{toTime}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List> generatePdfReport(@PathVariable("id") long id,
			@PathVariable("fromDate") String fromDate, @PathVariable("toDate") String toDate,
			@PathVariable("fromTime") String fromTime, @PathVariable("toTime") String toTime)
			throws MyTeamException, ParseException {
		List result = attendanceService.generatePdfReport(id, fromDate, toDate, fromTime, toTime);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "attendanciesReport/{reportDate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AttendenceData>> attendanciesReport(@PathVariable("reportDate") String reportDate,
			@RequestParam(value = "shift", required = false, defaultValue = "All") String shift)
			throws MyTeamException, SQLException {
		List<AttendenceData> lisOfAttendenceData = attendanceService.getAttendanciesReport(reportDate, shift);
		return new ResponseEntity<>(lisOfAttendenceData, HttpStatus.OK);
	}

	@RequestMapping(value = "employeesDataSave/{searchDate}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> employeesDataSave(@PathVariable("searchDate") String searchDate)
			throws MyTeamException {
		Boolean result = attendanceService.fetchEmployeesData(searchDate, false);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "resyncMonthData/{fromDate}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> resyncMonthData(@PathVariable("fromDate") String fromDate) throws MyTeamException {
		Boolean result = attendanceService.fetchEmployeesData(fromDate, true);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}