package com.nisum.myteam.controller;


import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.exception.handler.ResponseDetails;
import com.nisum.myteam.model.dao.EffectiveLoginData;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.service.IEffectiveLoginTimeService;
import com.nisum.myteam.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class EffectiveLoginTimeController {

    @Autowired
    IEffectiveLoginTimeService effectiveLoginTimeService;

    @Autowired
    private IEmployeeService employeeService;

    @Transactional
    @PostMapping("/updateLogins")
    public List<EffectiveLoginData> getLoginData(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) throws MyTeamException, ParseException {

        effectiveLoginTimeService.deleteLogins(date);
        List<EffectiveLoginData> loginData = new ArrayList<>();
        List<Employee> allEmployees = employeeService.getActiveEmployees();
        for(Employee employee:allEmployees){
            EffectiveLoginData effectiveLoginData =
                    effectiveLoginTimeService.calculateEffectiveLoginTimeForEmpAndSave(employee,date);
            loginData.add(effectiveLoginData);
        }
        return loginData;
    }
    @DeleteMapping("/deleteLogins")
    public void deleteLoginDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) throws ParseException {
        effectiveLoginTimeService.deleteLogins(date);
    }
    @GetMapping("/effectiveLogin")
    public Map<String,Object> getAllEmployeesLoginData(
            @RequestParam("employeeId") long employeeId,
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate) throws ParseException {

        return effectiveLoginTimeService.getEmployeesEffLoginData(employeeId,fromDate,toDate);
    }


//    @DeleteMapping("/deleteLoginData")
//    public ResponseEntity<?> deleteLoginData(@RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
//                                             @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate, @RequestParam("employeeId") String employeeId, HttpServletRequest request) throws MyTeamException {
//        effectiveLoginTimeService.deleteEmployeeLoginData(employeeId,fromDate,toDate);
//        ResponseDetails responseDetails = new ResponseDetails(new Date(), 904, "delete Employees Login details Successfully",
//                "delete Employees Login details Successfully", null, request.getRequestURI(), "Login details", null);
//        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);
//    }
//    @PostMapping("/syncLoginDataByDateRange")
//    public List<EffectiveLoginData> syncLoginDataByDateRange(@RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
//                                                 @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate) throws MyTeamException {
//        List<EffectiveLoginData> loginData = new ArrayList<>();
//        List<Employee> allEmployees = employeeService.getActiveEmployees();
//        //allEmployees.removeIf(s -> !"40270".equalsIgnoreCase(s.getEmployeeId()));
//        for (Employee employee : allEmployees) {
//            EffectiveLoginData effectiveLoginData =
//                    effectiveLoginTimeService.calculateEffectiveLoginTimeForEmpAndSave(employee, fromDate, toDate);
//            loginData.add(effectiveLoginData);
//        }
//        return loginData;
//    }
}
