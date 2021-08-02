package com.nisum.myteam.service.impl;

import com.nisum.myteam.model.dao.SchedulersLogsDetails;
import com.nisum.myteam.repository.SchedulersLogsDetailsRepo;
import com.nisum.myteam.service.ISchedulersLogsDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class SchedulersLogsDetailsService implements ISchedulersLogsDetailsService {

    @Autowired
    private SchedulersLogsDetailsRepo schedulersLogsDetailsRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public SchedulersLogsDetails getCurrentSchedulerLogDetails(String schedulerName, Date createdDate) {

         Query query = new Query(Criteria.where("schedulerName").in(schedulerName).and("Date").in(parseDate(new Date(), "yyyy-MM-dd")));
         SchedulersLogsDetails schedulersLogsDetails = mongoTemplate.findOne(query, SchedulersLogsDetails.class);
       // SchedulersLogsDetails schedulersLogsDetails = schedulersLogsDetailsRepo.findBySchedulerNameAndCreatedDate(schedulerName, getCurrentDate());
        return schedulersLogsDetails;
    }

    @Override
    public void saveSchedulersLog(SchedulersLogsDetails schedulersLogsDetails) {
        schedulersLogsDetailsRepo.save(schedulersLogsDetails);
    }

    public static String parseDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

}
