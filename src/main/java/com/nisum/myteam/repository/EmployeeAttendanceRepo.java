package com.nisum.myteam.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.EmpLoginData;

public interface EmployeeAttendanceRepo extends MongoRepository<EmpLoginData, Long> {

}
