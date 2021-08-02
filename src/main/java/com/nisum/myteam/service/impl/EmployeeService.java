package com.nisum.myteam.service.impl;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.*;
import com.nisum.myteam.repository.EmployeeRepo;
import com.nisum.myteam.service.*;
import com.nisum.myteam.utils.MyTeamUtils;
import com.nisum.myteam.utils.constants.ApplicationRole;
import com.nisum.myteam.utils.constants.RoleConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeService implements IEmployeeService {


    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IProjectService projectService;

    @Autowired
    private IDomainService domainService;

    @Autowired
    private IEmployeeRoleService employeeRoleService;

    @Autowired
    private EmployeeLocationService empLocationService;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private ISubStatusService subStatusService;

    public Map<String, String> response = new HashMap<>();

    @Override
    public Employee createEmployee(Employee employee, String loginEmpId) throws MyTeamException {
        employee.setCreatedOn(new Date());
        employee.setCreatedBy(loginEmpId);
        employee.setModifiedBy(loginEmpId);

        // adding employee to Bench Allocation
        resourceService.addResourceToBenchProject(employee, loginEmpId);

        // Saving employee Location Details.
        empLocationService.save(employee);

        return employeeRepo.save(employee);
    }

//    @Override
//    public Employee updateEmployee(Employee employeeReq, String loginEmpId) throws ParseException {
//        response.put("messege" , "Employee has been updated");
//        // update all emp details to inactive if employee is inactive
//        Query query = new Query(Criteria.where("employeeId").is(employeeReq.getEmployeeId()));
//        Update update = new Update();
//        update.set("employeeName", employeeReq.getEmployeeName());
//        update.set("emailId", employeeReq.getEmailId());
//        update.set("role", employeeReq.getRole());
//        update.set("gender", employeeReq.getGender());
//        update.set("functionalGroup", employeeReq.getFunctionalGroup());
//        update.set("empStatus", employeeReq.getEmpStatus());
////        update.set("empSubStatus", employeeReq.getEmpSubStatus());
//        update.set("employmentType", employeeReq.getEmploymentType());
//        update.set("empLocation", employeeReq.getEmpLocation());
////        update.set("domain", employeeReq.getDomain());
//        update.set("designation", employeeReq.getDesignation());
//        update.set("dateOfBirth", employeeReq.getDateOfBirth());
//        update.set("dateOfJoining", employeeReq.getDateOfJoining());
//        update.set("lastModifiedOn", new Date());
//        update.set("hasPassort", employeeReq.getHasPassort());
//        update.set("hasB1", employeeReq.getHasB1());
//        update.set("passportExpiryDate", employeeReq.getPassportExpiryDate());
//        update.set("b1ExpiryDate", employeeReq.getB1ExpiryDate());
//
//        update.set("modifiedBy", loginEmpId);
//
//        if (employeeReq.getEmpStatus().equalsIgnoreCase(MyTeamUtils.IN_ACTIVE_SPACE)) {
//            update.set("endDate", employeeReq.getEndDate());
////            update.set("empSubStatus", null);
//        }
//        // update employee location
//        if (employeeReq.getEmpLocation() != null && !employeeReq.getEmpLocation().equals("")) {
//            Employee existingEmployee = employeeRepo.findByEmployeeId(employeeReq.getEmployeeId());
//            if (!existingEmployee.getEmpLocation().equals(employeeReq.getEmpLocation())) {
//                empLocationService.update(employeeReq, false);
//            }
//
//        }
//
//        //update substatus
//        if(employeeReq.getEmpSubStatus()!=null) {
//            HashMap<String, Object> substatus = (LinkedHashMap) employeeReq.getEmpSubStatus();
//            if (substatus.keySet().contains("subStatus") && substatus.get("subStatus") != null) {
//                EmployeeSubStatus latestSubStatus = new EmployeeSubStatus();
//                latestSubStatus.setEmployeeID(substatus.get("employeeID").toString());
//                latestSubStatus.setSubStatus(substatus.get("subStatus").toString());
//                latestSubStatus.setFromDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(substatus.get("fromDate").toString()));
//                latestSubStatus.setToDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(substatus.get("toDate").toString()));
//                EmployeeSubStatus currentSubStatus = subStatusService.getCurrentSubStatus(employeeReq.getEmployeeId());
//                if (currentSubStatus != null) {
//                    if (currentSubStatus.getSubStatus().equals(latestSubStatus.getSubStatus())) {
//                        latestSubStatus.setId(currentSubStatus.getId());
//                        subStatusService.updateExistingEmplyeeSubStatus(loginEmpId,latestSubStatus);
//                    } else {
//                        response.put("messege", "Employee is already " + currentSubStatus.getSubStatus() + " from " + currentSubStatus.getFromDate() + " to " + currentSubStatus.getToDate());
//                    }
//                } else {
////                currentSubStatus = subStatusService.getLatestEmployeeSubStatus(employeeReq.getEmployeeId());
////                if(currentSubStatus == null)
//                    subStatusService.addEmployeeSubStatus(loginEmpId,latestSubStatus);
////                else {
////                    latestSubStatus.setId(currentSubStatus.getId());
////                    subStatusService.updateExistingEmplyeeSubStatus(latestSubStatus);
////                }
//                }
//            }
//        }
//
//
//        // update employee details
//        FindAndModifyOptions options = new FindAndModifyOptions();
//        options.returnNew(true);
//        options.upsert(true);
//        Employee employeeUpdated = mongoTemplate.findAndModify(query, update, options, Employee.class);
//
//        try {
//            // add to resource collection
//            //resourceService.addResources(employeeUpdated, loginEmpId);
//
//            // inactive the employee from the assigned project.
//            //resourceService.inactivateResource(employeeReq, employeeUpdated, loginEmpId);
//
//        } catch (Exception e) {
//        }
//        return employeeUpdated;
//    }

    @Override
    @Transactional
    public Employee updateEmployee(Employee employeeReq, String loginEmpId) throws ParseException {

        // update all emp details to inactive if employee is inactive
        Query query = new Query(Criteria.where("employeeId").is(employeeReq.getEmployeeId()));
        Update update = new Update();
        update.set("employeeName", employeeReq.getEmployeeName());
        update.set("mobileNumber", employeeReq.getMobileNumber());
        update.set("emailId", employeeReq.getEmailId());
        update.set("role", employeeReq.getRole());
        update.set("gender", employeeReq.getGender());
        update.set("functionalGroup", employeeReq.getFunctionalGroup());
        update.set("empStatus", employeeReq.getEmpStatus());
        update.set("employmentType", employeeReq.getEmploymentType());
        update.set("empLocation", employeeReq.getEmpLocation());
        update.set("designation", employeeReq.getDesignation());
        update.set("dateOfBirth", employeeReq.getDateOfBirth());
        update.set("dateOfJoining", employeeReq.getDateOfJoining());
        update.set("lastModifiedOn", new Date());
        update.set("hasPassort", employeeReq.getHasPassort());
        update.set("hasB1", employeeReq.getHasB1());
        update.set("passportExpiryDate", employeeReq.getPassportExpiryDate());
        update.set("b1ExpiryDate", employeeReq.getB1ExpiryDate());

        update.set("modifiedBy", loginEmpId);

        if (employeeReq.getEmpStatus().equalsIgnoreCase(MyTeamUtils.IN_ACTIVE_SPACE)) {
            update.set("endDate", employeeReq.getEndDate());
        }
        // update employee location
        if (employeeReq.getEmpLocation() != null && !employeeReq.getEmpLocation().equals("")) {
            Employee existingEmployee = employeeRepo.findByEmployeeId(employeeReq.getEmployeeId());
            if (!existingEmployee.getEmpLocation().equals(employeeReq.getEmpLocation())) {
                empLocationService.update(employeeReq, false);
            }

        }

        //update substatus
        if (employeeReq.getEmpSubStatus() != null) {
            HashMap<String, Object> substatus = (LinkedHashMap) employeeReq.getEmpSubStatus();
            if (substatus.keySet().contains("subStatus") && substatus.get("subStatus") != null) {
                EmployeeSubStatus latestSubStatus = new EmployeeSubStatus();
                latestSubStatus.setEmployeeID(substatus.get("employeeID").toString());
                latestSubStatus.setSubStatus(substatus.get("subStatus").toString());
                latestSubStatus.setFromDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(substatus.get("fromDate").toString()));
                latestSubStatus.setToDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(substatus.get("toDate").toString()));
                EmployeeSubStatus currentSubStatus = subStatusService.getCurrentSubStatus(employeeReq.getEmployeeId());
                if (currentSubStatus != null) {
                    if (currentSubStatus.getSubStatus().equals(latestSubStatus.getSubStatus())) {
                        latestSubStatus.setId(currentSubStatus.getId());
                        subStatusService.updateExistingEmplyeeSubStatus(loginEmpId, latestSubStatus);
                    } else {
                        response.put("messege", "Employee is already " + currentSubStatus.getSubStatus() + " from " + currentSubStatus.getFromDate() + " to " + currentSubStatus.getToDate());
                    }
                } else {
                    currentSubStatus = subStatusService.getLatestEmployeeSubStatus(employeeReq.getEmployeeId());
                    if (currentSubStatus != null && !currentSubStatus.getFromDate().before(new Date())) {
                        latestSubStatus.setId(currentSubStatus.getId());
                        subStatusService.updateExistingEmplyeeSubStatus(loginEmpId, latestSubStatus);
                    } else {
                        subStatusService.addEmployeeSubStatus(loginEmpId, latestSubStatus);

                    }


                }
            }
        }


        // update employee details
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        options.upsert(true);
        if (employeeReq.getEmpStatus().equals("In Active")) {
            resourceService.makeResourceInactive(employeeReq.getEmployeeId(), employeeReq.getEndDate());
            update.set("emailId", employeeReq.getEmailId()+MyTeamUtils._OLD);
            update.set("employeeId", employeeReq.getEmployeeId()+MyTeamUtils._OLD);
        }
        Employee employeeUpdated = mongoTemplate.findAndModify(query, update, options, Employee.class);
        response.put("messege", "Employee has been updated");
        return employeeUpdated;
    }


    @Override
    public Employee deleteEmployee(String employeeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("employeeId").is(employeeId));
        Update update = new Update();
        update.set("empStatus", "InActive");
        Employee employeeUpdated = mongoTemplate.findAndModify(query, update, Employee.class);
        log.info("The Deletion operation Result::" + employeeUpdated);
        return employeeUpdated;
    }

    @Override
    public Employee updateProfile(Employee employeeReq) throws MyTeamException {
        boolean mobileNumberChanged = false;
        employeeReq.setLastModifiedOn(new Date());
        Employee existingEmployee = employeeRepo.findByEmployeeId(employeeReq.getEmployeeId());
        String newMobileNumber = employeeReq.getMobileNumber();
        if (newMobileNumber != null && !newMobileNumber.equalsIgnoreCase("")) {
            if ((existingEmployee != null && existingEmployee.getMobileNumber() != null
                    && !existingEmployee.getMobileNumber().equalsIgnoreCase(newMobileNumber))
                    || (existingEmployee.getMobileNumber() == null)) {
                mobileNumberChanged = true;
            }
        }
        existingEmployee.setMobileNumber(employeeReq.getMobileNumber());
        existingEmployee.setAlternateMobileNumber(employeeReq.getAlternateMobileNumber());
        existingEmployee.setPersonalEmailId(employeeReq.getPersonalEmailId());
        existingEmployee.setBaseTechnology(employeeReq.getBaseTechnology());
        existingEmployee.setTechnologyKnown(employeeReq.getTechnologyKnown());
        Employee employeePersisted = employeeRepo.save(existingEmployee);
        return employeePersisted;

    }

    public boolean isEmployeeExistsById(String employeeId) {
        Employee employeeFound = getEmployeeById(employeeId);
        return (employeeFound == null) ? false : true;

    }

    @Override
    public Employee getEmployeeById(String employeeId) {
        log.info("The employeeId::" + employeeId);

        Employee employee = employeeRepo.findByEmployeeId(employeeId);
        log.info("Employee Found in Repo::" + employee);
        return employee;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }

    @Override
    public Employee getEmployeeByEmaillId(String emailId) {
        return employeeRepo.findByEmailId(emailId);
    }

    @Override
    public List<Employee> getEmployeesByEmpStatusAndShift(String empStatus, String shift) {
        return employeeRepo.findByEmpStatusAndShiftLikeOrderByEmployeeIdDesc(empStatus, shift);
    }

    @Override
    public List<Employee> getEmployeesByDesignation(String designation) {
        return employeeRepo.findByDesignation(designation);
    }

    @Override
    public int getEmployeesCountByDesignation(String designation) {
        return employeeRepo.countByDesignation(designation);
    }

    public List<Employee> getManagers() throws MyTeamException {
        List<Employee> activeEmpsList = getActiveEmployees();
        List<Employee> managers = activeEmpsList.stream()
                .filter(employee -> (RoleConstant.DIRECTOR.getRoleName().equalsIgnoreCase(employee.getRole())
                        || RoleConstant.DELIVERY_MANAGER.getRoleName().equalsIgnoreCase(employee.getRole())
                        || RoleConstant.MANAGER.getRoleName().equalsIgnoreCase(employee.getRole())
                        || RoleConstant.HR_MANAGER.getRoleName().equalsIgnoreCase(employee.getRole())
                        || RoleConstant.LEAD.getRoleName().equalsIgnoreCase(employee.getRole())))
                .sorted(Comparator.comparing(Employee::getEmployeeName)).collect(Collectors.toList());
        return managers;
    }

    @Override
    public List<Employee> getActiveEmployees() throws MyTeamException {
        return employeeRepo.findByEmpStatus(MyTeamUtils.ACTIVE);
    }

    @Override
    public List<Employee> getEmployeesByStatus(String status) {
//        if (status.equals("all")) {
//            return employeeRepo.findAll(new Sort(Sort.Direction.ASC, "employeeName"));
//        } else if (status.equals("Vacation")) {
//            Query query = new Query();
//            query.addCriteria(Criteria.where("empSubStatus").ne("Resigned").andOperator(Criteria.where("empSubStatus").ne(null),Criteria.where("empSubStatus").ne("")));
//            return mongoTemplate.find(query, Employee.class);
//        } else if (status.equals("Resigned")) {
//            return employeeRepo.findByEmpSubStatusOrderByEmployeeNameAsc("Resigned");
//        } else {
//            return employeeRepo.findByEmpStatus(status);
//        }
        List<Employee> employeesList = employeeRepo.findAll(new Sort(Sort.Direction.ASC, "employeeName"));
        employeesList.stream().forEach(e -> {
            EmployeeSubStatus subStatus = subStatusService.getCurrentSubStatus(e.getEmployeeId());
            if (subStatus != null)
                e.setEmpSubStatus((EmployeeSubStatus) subStatus);
            else
                e.setEmpSubStatus(null);
        });
        if (status.equals("all")) {
            return employeesList;
        } else if (status.equals("Vacation")) {

            return employeesList.stream().filter(e -> e.getEmpSubStatus() != null ||
                    (e.getEmpSubStatus() != null && !e.getEmpSubStatus().equals("Resigned")) ||
                    (e.getEmpSubStatus() != null && !e.getEmpSubStatus().equals(""))).collect(Collectors.toList());
        } else if (status.equals("Resigned")) {
            return employeeRepo.findByEmpSubStatusOrderByEmployeeNameAsc("Resigned");
        } else {
            return employeesList.stream().filter(e->Optional.ofNullable(e.getEmpStatus()).isPresent() && e.getEmpStatus().equals(status)).collect(Collectors.toList());

        }

    }


    @Override
    public List<Account> getAccounts() throws MyTeamException {
        return accountService.getAllAccounts();

    }

    @Override
    public Employee getEmployeeRoleDataForSearchCriteria(String searchId, String searchAttribute) {
        if (MyTeamUtils.EMPLOYEE_NAME.equals(searchAttribute)) {
            return employeeRepo.findByEmployeeName(searchId);
        } else if (MyTeamUtils.EMAIL_ID.equals(searchAttribute)) {
            return employeeRepo.findByEmailId(searchId);
        }
        return null;

    }

    @Override
    public List<String> getEmployeeDetailsForAutocomplete() {
        List<Employee> employeeList = employeeRepo.findAll();
        List<String> resultList = new ArrayList<>();
        employeeList.stream().sorted(Comparator.comparing(Employee::getEmployeeId))
                .collect(Collectors.toList()).forEach(employee -> {
            resultList.add(employee.getEmployeeId());
        });
        employeeList.stream().sorted(Comparator.comparing(Employee::getEmployeeName))
                .collect(Collectors.toList()).forEach(employee -> {
            resultList.add(employee.getEmployeeName());
        });
        employeeList.stream().sorted(Comparator.comparing(Employee::getEmailId)).collect(Collectors.toList())
                .forEach(employee -> {
                    resultList.add(employee.getEmailId());
                });
        return resultList;
    }

    @Override
    public List<HashMap<String, String>> getDeliveryLeads(String domainId) {
        Domain domain = domainService.getDomainById(domainId);
        return getDeliveryManagerMap(domain.getDeliveryManagers());
    }

    @Override
    public List<HashMap<String, String>> getDeliveryManagerMap(List deliveryManagerIdsList) {
        List<HashMap<String, String>> employeeList = new ArrayList<>();

        Query query = new Query(Criteria.where("employeeId").in(deliveryManagerIdsList));
        List<Employee> employeePersistedList = mongoTemplate.find(query, Employee.class);
        for (Employee employee : employeePersistedList) {
            HashMap<String, String> managerMap = new HashMap<>();
            managerMap.put("employeeId", employee.getEmployeeId());
            managerMap.put("employeeName", employee.getEmployeeName());
            employeeList.add(managerMap);
        }
        return employeeList;
    }


    @Override
    public List<Employee> getEmployeesByFunctionalGrp(String functionalGrp) {
        return employeeRepo.findByFunctionalGroup(functionalGrp);

    }

    @Override
    public boolean verifyEmployeeRole(String empId, String roleIdReq) {
        boolean flag = false;

        log.info("The employeeId::" + empId);

        Employee employee = getEmployeeById(empId);
        log.info("Employee::::in EmployeeService::" + employee);

        String roleName = employee.getRole();
        log.info("The employee role::" + roleName);
        if (StringUtils.isNotBlank(roleName) && !ApplicationRole.ADMIN.getRoleName().equalsIgnoreCase(roleName)) {

            if (ApplicationRole.FUNCTIONAL_MANAGER.getRoleName().equalsIgnoreCase(roleName) ||
                    ApplicationRole.DELIVERY_LEAD.getRoleName().equalsIgnoreCase(roleName)) {
                flag = true;
                log.info("in if block");
            } else {
                log.info("in else block");
                Set<String> roleSet = employeeRoleService.empRolesMapInfoByEmpId(empId);
                if (null != roleSet && !roleSet.isEmpty() && MyTeamUtils.INT_ZERO < roleSet.size()) {
                    if (roleSet.contains(roleIdReq)) {
                        flag = true;
                    }
                }

            }


        }
        log.info("before return flag::" + flag);
        return flag;
    }

    public List<Employee> getEmployeesFromList(Set<String> empIdsSet) {
        return employeeRepo.findByEmployeeIdIn(empIdsSet);

    }

    public Employee findByEmployeeId(String employeeId) {
        // TODO Auto-generated method stub
        return employeeRepo.findByEmployeeId(employeeId);
    }

	public List<Employee> getAllEmployeeListByFunGrps(List<String> functionalGroupList, Date onDate){
        Query query = new Query(Criteria.where("functionalGroup").in(functionalGroupList));
        List<Employee> employeePersistedList = mongoTemplate.find(query, Employee.class);
        employeePersistedList.removeIf(e -> !filterEmployeesByDate(onDate, e));
        return employeePersistedList;
    }

    private boolean filterEmployeesByDate(Date onDate, Employee e) {
        return onDate.after(e.getDateOfJoining()) &&  Optional.ofNullable(e.getEndDate()).isPresent() ? onDate.before(e.getEndDate()):true;
    }

    @Override
    public Employee uploadProfile(String empId, MultipartFile file) throws MyTeamException, IOException {
        Employee existingEmployee = employeeRepo.findByEmployeeId(empId);
        //  Employee employee=new Employee();
        // employee.setEmployeeId(empId);
        existingEmployee.setUpdateProfile(file.getBytes());
        existingEmployee.setLastUpdateProfile(new Date());
        Employee employeePersisted = employeeRepo.save(existingEmployee);
        return employeePersisted;
    }

    @Override
    public byte[] getUploadFile(String empId) {
        Employee  emp=  employeeRepo.findByEmployeeId(empId);

        return emp.getUpdateProfile();
    }

    @Override
    public Map<String, String> getEmployeeManagers(List<Employee> employeeList) {
        Map<String, String> employeeManager = new HashMap<>();
        Map<String, String> employeeNameMap = getAllEmployees().stream().distinct().collect(Collectors.toMap(e -> e.getEmployeeId(), e -> e.getEmployeeName()));
        employeeList.stream().forEach(employee -> {
                    Resource resource = resourceService.getLatestResourceByEmpId(employee.getEmployeeId());
                    Project project = projectService.getProjectByProjectId(resource.getProjectId());
                    employeeManager.put(employee.getEmployeeId(), project.getDeliveryLeadIds().stream().map(n -> employeeNameMap.get(n)).collect(Collectors.joining()));
                }
        );
        return employeeManager;
    }

}
