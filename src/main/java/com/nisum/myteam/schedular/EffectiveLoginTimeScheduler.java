package com.nisum.myteam.schedular;


import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.EffectiveLoginData;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.service.IEffectiveLoginTimeService;
import com.nisum.myteam.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class EffectiveLoginTimeScheduler {

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private IEffectiveLoginTimeService effectiveLoginTimeService;


    @Scheduled(cron = "${effective.login.time.cron}")
    @Transactional
    public void calculateLoginTime() throws MyTeamException {
        List<Employee> allEmployees = employeeService.getActiveEmployees();
        for(Employee employee:allEmployees){
            EffectiveLoginData effectiveLoginData =
                    effectiveLoginTimeService.calculateEffectiveLoginTimeForEmpAndSave(employee,null);
        }
    }
}
