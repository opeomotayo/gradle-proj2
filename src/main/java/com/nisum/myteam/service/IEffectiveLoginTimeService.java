package com.nisum.myteam.service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.EffectiveLoginData;
import com.nisum.myteam.model.dao.Employee;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

public interface IEffectiveLoginTimeService {

    public EffectiveLoginData calculateEffectiveLoginTimeForEmpAndSave(Employee employee, Date date) throws MyTeamException;

    public Map<String,Object> getEmployeesEffLoginData(long employeeId, Date fromDate, Date toDate) throws ParseException;

    public void deleteLogins(Date date) throws ParseException;

//    public void deleteEmployeeLoginData(String employeeId, Date fromDate, Date toDate) throws  MyTeamException;
//
//    EffectiveLoginData calculateEffectiveLoginTimeForEmpAndSave(Employee employee, Date fromDate, Date toDate) throws MyTeamException;
}
