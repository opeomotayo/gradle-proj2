package com.nisum.myteam.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.EmployeeVisa;

public interface EmployeeVisaRepo extends MongoRepository<EmployeeVisa, String> {
	List<EmployeeVisa> findByVisaName(String visaName);
}