package com.nisum.myteam.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Role;
import com.nisum.myteam.repository.RoleRepo;
import com.nisum.myteam.service.IRoleService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleService implements IRoleService {

	@Autowired
	RoleRepo roleRepo;

	@Override
	public Role addRole(Role roleInfo) throws MyTeamException {

		return roleRepo.save(roleInfo);
	}

	@Override
	public String getRole(String roleName) throws MyTeamException {

		return roleRepo.findByRoleName(roleName).getRoleId();
	}

}
