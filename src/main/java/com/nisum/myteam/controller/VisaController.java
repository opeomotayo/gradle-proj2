package com.nisum.myteam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.EmployeeVisa;
import com.nisum.myteam.model.dao.TravelRequest;
import com.nisum.myteam.model.dao.Visa;
import com.nisum.myteam.service.IVisaService;

@RestController
@RequestMapping("/visa")
public class VisaController {

	@Autowired
	private IVisaService visaService;

	@RequestMapping(value = "/getAllVisas", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Visa>> getAllVisas() throws MyTeamException {
		List<Visa> visas = visaService.getAllVisas();
		return new ResponseEntity<>(visas, HttpStatus.OK);
	}

	@RequestMapping(value = "/getAllEmployeeVisas", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmployeeVisa>> getAllEmployeeVisas() throws MyTeamException {
		List<EmployeeVisa> employeeVisas = visaService.getAllEmployeeVisas();
		return new ResponseEntity<>(employeeVisas, HttpStatus.OK);
	}

	@RequestMapping(value = "/addEemployeeVisa", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeVisa> addEemployeeVisa(@RequestBody EmployeeVisa employeeVisa)
			throws MyTeamException {
		EmployeeVisa visa = visaService.addEmployeeVisas(employeeVisa);
		return new ResponseEntity<>(visa, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateEemployeeVisa", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeVisa> updateEemployeeVisa(@RequestBody EmployeeVisa eVisa) throws MyTeamException {
		EmployeeVisa visa = visaService.updateEmployeeVisas(eVisa);
		return new ResponseEntity<>(visa, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteEemployeeVisa", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deleteEemployeeVisa(@RequestBody EmployeeVisa eVisa) throws MyTeamException {
		visaService.deleteEmployeeVisas(eVisa);
		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/getAllTravelRequests", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TravelRequest>> getAllTravelRequests() throws MyTeamException {
		List<TravelRequest> employeeVisas = visaService.getAllTravels();
		return new ResponseEntity<>(employeeVisas, HttpStatus.OK);
	}

	@RequestMapping(value = "/addTravelRequest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TravelRequest> addTravelRequest(@RequestBody TravelRequest employeeVisa)
			throws MyTeamException {
		TravelRequest visa = visaService.addTravelRequest(employeeVisa);
		return new ResponseEntity<>(visa, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateTravelRequest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TravelRequest> updateTravelRequest(@RequestBody TravelRequest eVisa) throws MyTeamException {
		TravelRequest visa = visaService.updateTravelRequest(eVisa);
		return new ResponseEntity<>(visa, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteTravelRequest", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deleteTravelRequest(@RequestBody TravelRequest eVisa) throws MyTeamException {
		visaService.deleteEmployeeVisas(eVisa);
		return new ResponseEntity<>("Success", HttpStatus.OK);
	}
}

