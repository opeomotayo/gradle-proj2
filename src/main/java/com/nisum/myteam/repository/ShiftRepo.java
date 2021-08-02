package com.nisum.myteam.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.Shift;

public interface ShiftRepo extends MongoRepository<Shift, String> {
	
} 