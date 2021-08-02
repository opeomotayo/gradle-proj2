package com.nisum.myteam.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.Skill;


public interface SkillRepo extends MongoRepository<Skill, String> {
	
} 