package com.nisum.myteam.service;

import com.nisum.myteam.model.dao.EmployeeShift;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface IEmployeeShiftService {
	
	//public void addEmployeeShift(Resource resource, String loginEmpId);
	
	//void updateEmployeeShift(Resource existingTeammate, String loginEmpId);

    public List<EmployeeShift> getAllEmployeeShifts();

    public EmployeeShift getEmployeeShift(String employeeId);
	
}
