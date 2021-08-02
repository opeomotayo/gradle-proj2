package com.nisum.myteam.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.EmployeeShift;

public interface EmployeeShiftRepo extends MongoRepository<EmployeeShift, String> {

    public EmployeeShift findByEmployeeId(String employeeId);

} 