package com.nisum.myteam.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import com.nisum.myteam.model.dao.OrgLocation;
import com.nisum.myteam.service.IOrgLocationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OrgLocationController {

	@Autowired
	IOrgLocationService orgLocationService;

	// @RequestMapping(value = "/getLocations"
	@RequestMapping(value = "/organization/locations/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getLocations(HttpServletRequest request) throws MyTeamException {

		List<String> locationsList = orgLocationService.getLocations().stream()
				.filter(e -> (e.isActiveStatus() == true)).map(OrgLocation::getLocation).sorted()
				.collect(Collectors.toList());
		log.info("getLocations " + locationsList);

		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905,
				"Retrieved Organization Locations  successfully", "Organization Locations List", locationsList,
				request.getRequestURI(), "Organization Location Details", null);

		return new ResponseEntity<>(getRespDetails, HttpStatus.OK);

	}
	@RequestMapping(value = "/organization/locations/{country}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSelectedLocations(@PathVariable("country") String country, HttpServletRequest request) throws MyTeamException {

		List<String> locationsList = orgLocationService.getLocations().stream()
				.filter(e -> (e.isActiveStatus() == true)&&(e.getCountry().equalsIgnoreCase(country))).map(OrgLocation::getLocation).sorted()
				.collect(Collectors.toList());
		log.info("getLocations " + locationsList);

		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905,
				"Retrieved Organization Locations  successfully", "Organization Locations List", locationsList,
				request.getRequestURI(), "Organization Location Details", null);

		return new ResponseEntity<>(getRespDetails, HttpStatus.OK);

	}

	@RequestMapping(value = "/organization/countrys/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getCountrys(HttpServletRequest request) throws MyTeamException {
		log.info("getCountrys Start ");
		List<String> countryList = orgLocationService.getLocations().stream()
				.filter(e -> (e.isActiveStatus() == true)).map(OrgLocation::getCountry).distinct().sorted()
				.collect(Collectors.toList());
		log.info("getLocations " + countryList);

		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905,
				"Retrieved Organization Locations  successfully", "Organization Country List", countryList,
				request.getRequestURI(), "Organization Country Details", null);
		log.info("getCountrys End ");
		return new ResponseEntity<>(getRespDetails, HttpStatus.OK);

	}
}
