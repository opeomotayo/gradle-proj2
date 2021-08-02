package com.nisum.myteam.service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Account;
import com.nisum.myteam.model.dao.Employee;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Service
public interface IEmployeeService {

    public String messege = "";

    boolean isEmployeeExistsById(String employeeId);

    Employee createEmployee(Employee employeeRoles, String empId) throws MyTeamException;

    Employee updateEmployee(Employee employeeRoles, String empId) throws ParseException;

    Employee deleteEmployee(String empId);

    Employee updateProfile(Employee employeeRoles) throws MyTeamException;

    Employee getEmployeeById(String empId);

    Employee getEmployeeByEmaillId(String emailId);

    List<Employee> getManagers() throws MyTeamException;

    List<Employee> getActiveEmployees() throws MyTeamException;

    List<Employee> getEmployeesByStatus(String status);

    List<Account> getAccounts() throws MyTeamException;

    Employee getEmployeeRoleDataForSearchCriteria(String searchId, String searchAttribute);

    List<String> getEmployeeDetailsForAutocomplete();

    List<HashMap<String, String>> getDeliveryLeads(String domainId);

    List<Employee> getEmployeesByFunctionalGrp(String functionalGrp);

    boolean verifyEmployeeRole(String empId, String roleName);

    List<Employee> getEmployeesFromList(Set<String> empIdsSet);

    List<HashMap<String, String>> getDeliveryManagerMap(List deliveryManagerIdsList);

    public List<Employee> getAllEmployees();

    public List<Employee> getEmployeesByEmpStatusAndShift(String empStatus, String shift);

    public List<Employee> getAllEmployeeListByFunGrps(List<String> functionalGroupList, Date onDate);

    public List<Employee> getEmployeesByDesignation(String designation);

    public int getEmployeesCountByDesignation(String designation);

    Employee uploadProfile(String empId, MultipartFile file) throws MyTeamException, IOException;

    byte[] getUploadFile(String  empId);
    public Map<String, String> getEmployeeManagers(List<Employee> employeeList) throws MyTeamException;

}
