package com.nisum.myteam.service;

import org.springframework.stereotype.Service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Role;

@Service
public interface IRoleService {

	public Role addRole(Role roleInfo) throws MyTeamException;

	public String getRole(String roleName) throws MyTeamException;
}
