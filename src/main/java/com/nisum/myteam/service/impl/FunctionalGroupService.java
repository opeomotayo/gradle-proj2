package com.nisum.myteam.service.impl;

import com.nisum.myteam.model.FunctionalGroup;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.repository.FunctionalGroupRepo;
import com.nisum.myteam.service.IFunctionalGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class FunctionalGroupService implements IFunctionalGroupService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FunctionalGroupRepo functionalGroupRepo;

    public List<FunctionalGroup> getAllFunctionalGroups() {
        return functionalGroupRepo.findAll();
    }

    @Override
    public FunctionalGroup getFunctionalGroup(String functionalGroupName) {
        return functionalGroupRepo.findByName(functionalGroupName);
    }

    @Override
    public List<FunctionalGroup> getAllBillableFunctionalGroups() {
        Query query = new Query(Criteria.where("name").nin(Arrays.asList("IT","Recruiter","Admin","HR","Accounts","Delivery Org","Global Mobility")));
        List<FunctionalGroup> functionalGroupList = mongoTemplate.find(query, FunctionalGroup.class);
        return functionalGroupList;
    }
}
