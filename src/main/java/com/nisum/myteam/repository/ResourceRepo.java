package com.nisum.myteam.repository;


import com.nisum.myteam.model.dao.Resource;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ResourceRepo
        extends MongoRepository<Resource, String> {

    List<Resource> findByProjectId(String projectId);

    List<Resource> findByEmployeeId(String employeeId);

    Resource findById(ObjectId id);

    List<Resource> findByEmployeeIdAndProjectId(String employeeId, String projectId);

    List<Resource> findByBillableStatus(String resourceAllocationStatus);

    List<Resource> findByBillingStartDateGreaterThan(Date billingStartDate);
    Resource findOneByEmployeeIdAndStatus(String employeeId,String status);

    List<Resource> findByBillingStartDateBetween(Date fromDate,Date toDate);
    //Set<Resource> findByBillableStatus(String resourceAllocationStatus);


    Resource findOneByProjectIdAndEmployeeIdAndBillingEndDate(String projectId,String employeeId,Date billingEndDate);

    Resource findOneByEmployeeIdAndBillingEndDate(String employeeId,Date billingEndDate);

   // List<Resource> findByEmployeeIdAndActive(String employeeId, boolean status);

   // List<Resource> findByEmployeeIdAndProjectIdAndActive(String employeeId, String projectId, boolean status);

   // List<Resource> findByAccountAndActiveAndBillableStatus(String account, boolean status, String billableStatus);

}
