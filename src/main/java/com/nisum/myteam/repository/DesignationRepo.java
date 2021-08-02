package com.nisum.myteam.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.Designation;

public interface DesignationRepo extends MongoRepository<Designation, String> {
	
} 