package com.nisum.myteam.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.EmployeeEfforts;
import com.nisum.myteam.service.IEmployeeEffortsService;


@RestController
@RequestMapping("/employeeEfforts")
public class EmployeeEffortsController {

	@Autowired
	private IEmployeeEffortsService employeeEffortService;

	@RequestMapping(value = "getWeeklyReport", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmployeeEfforts>> employeeLoginsBasedOnDate(
			@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate) throws MyTeamException {
		List<EmployeeEfforts> employeeEffortsList = new ArrayList<>();
		try {
			employeeEffortsList = employeeEffortService.getEmployeeEffortsReport(fromDate, toDate); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(employeeEffortsList, HttpStatus.OK);
	}
	
}
