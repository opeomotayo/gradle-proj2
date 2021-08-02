package com.nisum.myteam.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.exception.handler.ResponseDetails;
import com.nisum.myteam.model.dao.EmployeeLocation;
import com.nisum.myteam.service.IEmployeeLocationService;

@RestController
public class EmployeeLocationController {

	@Autowired
	IEmployeeLocationService empLocationService;

	@RequestMapping(value = "/employees/locations/{employeeId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEmployeeLocations(@PathVariable("employeeId") String empId,HttpServletRequest request)
			throws MyTeamException {
		
		List<EmployeeLocation> employeeLocationDetails = empLocationService.getEmployeeLocations(empId);
		
		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 701, "Retrieved Employee Locations successfylly",
				"Employee Locations", employeeLocationDetails, request.getRequestURI(), "Employee Location Details", null);
		return new ResponseEntity<>(getRespDetails, HttpStatus.OK);
	}
}
