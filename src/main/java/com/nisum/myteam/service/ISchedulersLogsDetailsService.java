package com.nisum.myteam.service;

import com.nisum.myteam.model.dao.SchedulersLogsDetails;

import java.util.Date;

public interface ISchedulersLogsDetailsService {
    public SchedulersLogsDetails getCurrentSchedulerLogDetails(String schedulerName, Date createdDate);

    public void saveSchedulersLog(SchedulersLogsDetails schedulersLogsDetails);
}
