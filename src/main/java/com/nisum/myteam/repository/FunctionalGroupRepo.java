package com.nisum.myteam.repository;

import com.nisum.myteam.model.FunctionalGroup;
import com.nisum.myteam.model.FunctionalGroup;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FunctionalGroupRepo extends MongoRepository<FunctionalGroup, String> {

    public FunctionalGroup findByName(String functionalGroupName);

}
