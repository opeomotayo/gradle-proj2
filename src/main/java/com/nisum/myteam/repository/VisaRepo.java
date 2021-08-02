package com.nisum.myteam.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.Visa;

public interface VisaRepo extends MongoRepository<Visa, String> {
	
} 