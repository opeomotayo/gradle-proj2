package com.nisum.myteam.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.exception.handler.ResponseDetails;
import com.nisum.myteam.model.dao.Shift;
import com.nisum.myteam.service.IShiftService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ShiftController {

	@Autowired
	IShiftService shiftService;

	// @RequestMapping(value = "/getAllShifts"
	@RequestMapping(value = "/employees/shifts/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAllShifts(HttpServletRequest request) throws MyTeamException {

		List<String> shiftsList = shiftService.getAllShifts().stream()
				.filter(e -> "Y".equalsIgnoreCase(e.getActiveStatus())).map(Shift::getShiftName).sorted()
				.collect(Collectors.toList());
		log.info("The Active shift names:" + shiftsList);
		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905, "Retrieved Employee Shifts  successfully",
				"Employee Shifts List", shiftsList, request.getRequestURI(), "Employee Shifts Details", null);

		return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
	}

}
