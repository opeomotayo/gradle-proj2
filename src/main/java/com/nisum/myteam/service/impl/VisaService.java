/**
 * 
 */
package com.nisum.myteam.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nisum.myteam.model.dao.EmployeeVisa;
import com.nisum.myteam.model.dao.TravelRequest;
import com.nisum.myteam.model.dao.Visa;
import com.nisum.myteam.repository.EmployeeRepo;
import com.nisum.myteam.repository.EmployeeVisaRepo;
import com.nisum.myteam.repository.TravelRepo;
import com.nisum.myteam.repository.VisaRepo;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IVisaService;

/**
 * @author nisum
 *
 */
@Service
public class VisaService implements IVisaService {

    @Autowired
    private VisaRepo visaRepo;
    @Autowired
    private TravelRepo travelRepo;

    @Autowired
    private EmployeeVisaRepo employeeVisaRepo;

    @Autowired
    private IEmployeeService userService;
    
    @Autowired
    private EmployeeRepo employeeRepo;
    

    @Override
    public List<Visa> getAllVisas() {
        return visaRepo.findAll();
    }

    @Override
    public List<EmployeeVisa> getAllEmployeeVisas() {
        return employeeVisaRepo.findAll();
    }

    @Override
    public EmployeeVisa addEmployeeVisas(EmployeeVisa e) {
        return employeeVisaRepo.save(e);
    }

    @Override
    public EmployeeVisa updateEmployeeVisas(EmployeeVisa e) {
        return employeeVisaRepo.save(e);
    }

    @Override
    public void deleteEmployeeVisas(EmployeeVisa e) {
        employeeVisaRepo.delete(e);
    }

    @Override
    public List<TravelRequest> getAllTravels() {
        return travelRepo.findAll();
    }

    @Override
    public TravelRequest addTravelRequest(TravelRequest e) {
        return travelRepo.save(e);
    }

    @Override
    public TravelRequest updateTravelRequest(TravelRequest e) {
        return travelRepo.save(e);
    }

    @Override
    public void deleteEmployeeVisas(TravelRequest e) {
        travelRepo.delete(e);
    }

 }
