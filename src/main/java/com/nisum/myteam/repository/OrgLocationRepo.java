package com.nisum.myteam.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.OrgLocation;

public interface OrgLocationRepo extends MongoRepository<OrgLocation, String> {
	List<OrgLocation> findByLocationAndActiveStatus(String location, boolean activeStatus);
}