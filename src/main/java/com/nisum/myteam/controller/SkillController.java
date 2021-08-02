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
import com.nisum.myteam.model.dao.Skill;
import com.nisum.myteam.service.ISkillService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SkillController {

	@Autowired
	ISkillService skillService;

	// @RequestMapping(value = "/getSkills"
	@RequestMapping(value = "/employees/skills/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTechnologies(HttpServletRequest request) throws MyTeamException {
		List<String> skillsList = skillService.getTechnologies().stream()
				.filter(e -> "Y".equalsIgnoreCase(e.getActiveStatus())).map(Skill::getSkillName).sorted()
				.collect(Collectors.toList());
		log.info("The Employee skills Lis::" + skillsList);
		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905, "Retrieved Employee Skills  successfully",
				"Employee Skills List", skillsList, request.getRequestURI(), "Employee Skills Details", null);

		return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

	}
}
