package com.nisum.myteam.repository;

import com.nisum.myteam.model.dao.EmployeeSubStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmployeeSubStatusRepo extends MongoRepository<EmployeeSubStatus, String> {

    public List<EmployeeSubStatus> findByEmployeeID(String employeeId);
    public List<EmployeeSubStatus> findBySubStatus(String subStatus);

}
