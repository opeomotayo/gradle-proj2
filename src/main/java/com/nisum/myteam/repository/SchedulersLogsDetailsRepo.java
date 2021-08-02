package com.nisum.myteam.repository;

import com.nisum.myteam.model.dao.SchedulersLogsDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;

public interface SchedulersLogsDetailsRepo extends MongoRepository<SchedulersLogsDetails,String> {
    //SchedulersLogsDetails findBySchedulerNameAndCreatedDate(Date fromDate, Date toDate, String schedulerName);
    SchedulersLogsDetails findBySchedulerNameAndCreatedDate(String schedulerName, Date createdDate);
}
