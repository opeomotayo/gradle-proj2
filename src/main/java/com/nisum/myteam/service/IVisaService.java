/**
 * 
 */
package com.nisum.myteam.service;

import java.util.List;

import com.nisum.myteam.model.dao.EmployeeVisa;
import com.nisum.myteam.model.dao.TravelRequest;
import com.nisum.myteam.model.dao.Visa;

/**
 * @author nisum
 *
 */
public interface IVisaService {

	List<Visa> getAllVisas();

	List<EmployeeVisa> getAllEmployeeVisas();

	EmployeeVisa addEmployeeVisas(EmployeeVisa e);

	EmployeeVisa updateEmployeeVisas(EmployeeVisa e);

	void deleteEmployeeVisas(EmployeeVisa e);

	List<TravelRequest> getAllTravels();

	TravelRequest addTravelRequest(TravelRequest e);

	TravelRequest updateTravelRequest(TravelRequest e);

	void deleteEmployeeVisas(TravelRequest e);
}
