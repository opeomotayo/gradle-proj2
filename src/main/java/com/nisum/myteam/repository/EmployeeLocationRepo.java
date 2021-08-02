package com.nisum.myteam.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.EmployeeLocation;

public interface EmployeeLocationRepo extends MongoRepository<EmployeeLocation, String> {

	List<EmployeeLocation> findByEmployeeId(String employeeId);
	
	EmployeeLocation findByEmployeeName(String employeeName);
}
