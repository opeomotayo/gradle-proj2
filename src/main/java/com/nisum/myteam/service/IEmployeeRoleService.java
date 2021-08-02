package com.nisum.myteam.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mongodb.WriteResult;
import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.EmployeeRole;

@Service
public interface IEmployeeRoleService {

	List<EmployeeRole> findByEmployeeId(String employeeId);
	
	void addEmployeeRole(String employeeId, String roleId) throws MyTeamException;
	
	void saveUniqueEmployeeAndRole(List<String> employeeIds, String roleId) throws MyTeamException;

	WriteResult deleteRole(String employeeId, String roleId) throws MyTeamException;

	String getEmployeeRole(String employeeId);
	
	public Set<String> empRolesMapInfoByEmpId(String employeeId);
}
