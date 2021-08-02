package com.nisum.myteam.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.nisum.myteam.exception.handler.ResponseDetails;
import com.nisum.myteam.service.impl.FunctionalGroupService;

@RestController
@RequestMapping("/functionalGroups")
public class FunctionalGroupController {

	
	@Autowired
	FunctionalGroupService functionalGroupService;
	
	@RequestMapping(value = "/getAllFunctionalGroups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAllFunctionalGroups(HttpServletRequest request) {
		List<String> functionalGroupsList = new ArrayList<>();
		functionalGroupsList.addAll(functionalGroupService.getAllFunctionalGroups().stream().
                filter(f -> !Arrays.asList("IT","Recruiter","Admin","HR","Accounts").contains(f.getName())).map(f -> f.getName()).collect(Collectors.toList()));
		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905, "Retrieved FunctionalGroups  successfully",
				"FunctionalGroups list", functionalGroupsList, request.getRequestURI(), "FunctionalGroups Details: Status", null);
		return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
		

	}
}
