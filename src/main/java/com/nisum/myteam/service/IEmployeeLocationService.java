package com.nisum.myteam.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.dao.EmployeeLocation;

@Service
public interface IEmployeeLocationService {

	public void save(Employee employee);

	public List<EmployeeLocation> getEmployeeLocations(String empId);

	void update(Employee employee, boolean delete);
}
