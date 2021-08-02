package com.nisum.myteam.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.Role;

public interface RoleRepo extends MongoRepository<Role, String> {

	Role findByRoleName(String roleName);

	Role findByRoleId(String roleId);
}