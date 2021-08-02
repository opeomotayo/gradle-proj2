package com.nisum.myteam.service.impl;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.WriteResult;
import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.EmployeeRole;
import com.nisum.myteam.repository.EmployeeRoleRepo;
import com.nisum.myteam.repository.RoleRepo;
import com.nisum.myteam.service.IEmployeeRoleService;
import com.nisum.myteam.utils.MyTeamUtils;

@Service
public class EmployeeRoleService implements IEmployeeRoleService {

	@Autowired
	private EmployeeRoleRepo employeeRoleRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private MongoTemplate mongoTemplate;

	
	@Override
	public void saveUniqueEmployeeAndRole(List<String> employeeIds, String roleId) throws MyTeamException {
		for (String employeeId : employeeIds) {
			addEmployeeRole(employeeId, roleId);
		}
	}
	
	public void addEmployeeRole(String employeeId, String roleId) throws MyTeamException {
		EmployeeRole roleMappingInfo = employeeRoleRepo.findByEmployeeIdAndRoleId(employeeId, roleId);
		boolean isChanged = false;
		if (roleMappingInfo == null) {
			roleMappingInfo = new EmployeeRole();
			roleMappingInfo.setEmployeeId(employeeId);
			roleMappingInfo.setRoleId(roleId);
			roleMappingInfo.setIsActive("Y");
			isChanged = true;
		} else if (roleMappingInfo.getIsActive().equalsIgnoreCase("N")) {
			roleMappingInfo.setIsActive("Y");
			isChanged = true;
		}
		if (isChanged) {
			employeeRoleRepo.save(roleMappingInfo);
		}
	}

	@Override
	public List<EmployeeRole> findByEmployeeId(String employeeId)
	{
		return employeeRoleRepo.findByEmployeeId(employeeId);
		 
	}
	@Override
	public WriteResult deleteRole(String employeeId, String roleId) throws MyTeamException {
		Query query = new Query(Criteria.where("employeeId").is(employeeId).and("roleId").is(roleId));
		Update update = new Update();
		update.set(MyTeamUtils.IS_ACTIVE, "N");
		return mongoTemplate.upsert(query, update, EmployeeRole.class);
	}

	@Override
	public String getEmployeeRole(String employeeId) {
		Map<Integer, String> roleInfoMap = new LinkedHashMap<Integer, String>();
		String roleName = null;

		List<EmployeeRole> listOfEmployeeRoles = employeeRoleRepo.findByEmployeeId(employeeId).stream()
				.filter(e -> ("N".equalsIgnoreCase(e.getIsActive()))).collect(Collectors.toList());
		if (listOfEmployeeRoles != null && listOfEmployeeRoles.size() > 0) {
			for (EmployeeRole employee : listOfEmployeeRoles) {
				roleInfoMap.put((roleRepo.findByRoleId(employee.getRoleId())).getPriority(), employee.getRoleId());
			}
			roleInfoMap = roleInfoMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(
					Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
			if (!roleInfoMap.isEmpty()) {
				Map.Entry<Integer, String> entry = roleInfoMap.entrySet().iterator().next();
				roleName = roleRepo.findByRoleId(entry.getValue()).getRoleName();
			}
		}
		return roleName;
	}
	
	
	@Override
	public Set<String> empRolesMapInfoByEmpId(String employeeId) { 
		Set<String> roleSet = new HashSet<String>();
		List<EmployeeRole> listOfEmployeeRoles = employeeRoleRepo.findByEmployeeId(employeeId).stream().filter(e->(
			"N".equalsIgnoreCase(e.getIsActive()))).collect(Collectors.toList());
		
		if(null != listOfEmployeeRoles && !listOfEmployeeRoles.isEmpty() && MyTeamUtils.INT_ZERO  < listOfEmployeeRoles.size()) {
			for(EmployeeRole obj : listOfEmployeeRoles) {
				roleSet.add(obj.getRoleId());
			}
		}
		
		return roleSet;
	}
	

}
