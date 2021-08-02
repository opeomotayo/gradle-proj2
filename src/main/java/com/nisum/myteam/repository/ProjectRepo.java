package com.nisum.myteam.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.Project;

public interface ProjectRepo extends MongoRepository<Project, String> {

	Project findByProjectId(String projectId);

	Project findByProjectName(String projectName);

	List<Project> findByDeliveryLeadIds(String empId);

	// List<Project> findByManagerId(String managerId);

	List<Project> findByAccountIdIn(Set<String> accIdsSet);

	List<Project> findByAccountId(String accountId);

	List<Project> findByDomainId(String domainId);

}