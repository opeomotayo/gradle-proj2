package com.nisum.myteam.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.dao.EmployeeLocation;
import com.nisum.myteam.repository.EmployeeLocationRepo;
import com.nisum.myteam.service.IEmployeeLocationService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeLocationService implements IEmployeeLocationService {

	@Autowired
	private EmployeeLocationRepo employeeLocationRepo;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void save(Employee employee) {
		EmployeeLocation employeeLocation = new EmployeeLocation();
		employeeLocation.setActive(employee.getEmpStatus().equalsIgnoreCase("Active"));
		employeeLocation.setEmployeeId(employee.getEmployeeId());
		employeeLocation.setEmployeeName(employee.getEmployeeName());
		employeeLocation.setCreateDate(new Date());
		employeeLocation.setEndDate(new Date());
		employeeLocation.setUpdatedDate(new Date());
		employeeLocation.setStartDate(new Date());
		employeeLocation.setEmpLocation(employee.getEmpLocation());
		employeeLocationRepo.save(employeeLocation);
	}

	@Override
	public List<EmployeeLocation> getEmployeeLocations(String empId) {
		List<EmployeeLocation> empLocationList = employeeLocationRepo.findByEmployeeId(empId);
		log.info("The Employee Locations: findByEmployeeId" + empLocationList);
		return empLocationList;
	}

	@Override
	public void update(Employee employee, boolean delete) {
		try {
			Query getQuery = new Query();
			getQuery.addCriteria(new Criteria().andOperator(Criteria.where("active").is(true),
					Criteria.where("employeeId").is(employee.getEmployeeId())));
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			EmployeeLocation existingLocation = mongoTemplate.findOne(getQuery, EmployeeLocation.class);
			if (existingLocation != null) {
				existingLocation.setActive(false);
				existingLocation.setEndDate(cal.getTime());
				existingLocation.setUpdatedDate(new Date());
				mongoTemplate.save(existingLocation);
			}
			if (!delete)
				save(employee);
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
}
