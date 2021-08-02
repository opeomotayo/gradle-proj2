package com.nisum.myteam.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.EmployeeRole;

public interface EmployeeRoleRepo extends MongoRepository<EmployeeRole, String> {

	EmployeeRole findByEmployeeIdAndRoleId(String employeeId, String roleId);

	List<EmployeeRole> findByEmployeeId(String employeeId);
}