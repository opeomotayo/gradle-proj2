package com.nisum.myteam.repository;

import com.nisum.myteam.model.dao.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;


public interface EmployeeRepo
        extends MongoRepository<Employee, String> {
    Employee findByEmployeeIdAndEmpStatus(String employeeId, String empStatus);

    Employee findByEmailId(String emailId);

    Employee findByEmployeeId(String employeeId);

    Employee findByEmployeeName(String employeeName);

    List<Employee> findByEmpStatusAndFunctionalGroup(String empStatus,
                                                     String functionalGroup);

    List<Employee> findByEmpStatusOrderByEmployeeNameAsc(String empStatus);
    
    List<Employee> findByEmpStatus(String status);
    
    List<Employee> findByEmployeeIdIn(Set<String> empIdsSet);
    
    List<Employee> findByEmpSubStatusOrderByEmployeeNameAsc(String subStatus);

    List<Employee> findByEmpStatusAndShiftLikeOrderByEmployeeIdDesc(String status, String shift);

    List<Employee> findByDesignation(String designation);

    List<Employee> findByFunctionalGroup(String functionalGroup);
	
    int countByDesignation(String designation);
}
 
