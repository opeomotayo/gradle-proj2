package com.nisum.myteam.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.TravelRequest;

public interface TravelRepo extends MongoRepository<TravelRequest, String> {
	
} 