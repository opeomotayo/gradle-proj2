package com.nisum.myteam.service.impl;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.*;
import com.nisum.myteam.model.vo.EmployeeDashboardVO;
import com.nisum.myteam.model.vo.ResourceVO;
import com.nisum.myteam.service.IDashboardService;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IProjectService;
import com.nisum.myteam.service.IResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DashboardService implements IDashboardService {

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private IProjectService projectService;

    @Autowired
    private DomainService domainService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SubStatusService subStatusService;

    @Override
    public List<EmployeeDashboardVO> getEmployeesDashBoard() {
        //List<Employee> allEmployees = employeeRepo.findAll();
        List<Employee> allEmployees = employeeService.getAllEmployees();

        List<EmployeeDashboardVO> employeeDashboard = new ArrayList<>();
        Map<String, Object> resourcesMap = new HashMap();
        Map<String, Object> teamMatesStatusMap = new HashMap();

        // Find all active employees
        //List<Resource> resources = resourceService.getAllResources();

        List<ResourceVO> resources = resourceService.getAllResourcesVO();


        for (ResourceVO resource : resources) {
            //if (resource.isActive()) {
            if (resource.getBillingEndDate().compareTo(new Date()) > 0) {


                Project project = projectService.getProjectByProjectId(resource.getProjectId());
                if (project != null && project.getStatus() != null
                        && !"Completed".equalsIgnoreCase(project.getStatus())) {
                    Object projectResource = resourcesMap.get(resource.getEmployeeId());

                    if (projectResource == null) {
                        List listOfObjects = new ArrayList<>();
                        listOfObjects.add(resource);
                        // A person can have multiple active projects with billability
                        resourcesMap.put(resource.getEmployeeId(), listOfObjects);
                    } else {
                        List existingRecordsInMap = (List) resourcesMap
                                .get(resource.getEmployeeId());
                        existingRecordsInMap.add(resource);
                        resourcesMap.put(resource.getEmployeeId(),
                                existingRecordsInMap);

                    }
                }
            }
        }
        for (Employee emp : allEmployees) {
            if (resourcesMap.containsKey(emp.getEmployeeId())) {
                Object value = resourcesMap.get(emp.getEmployeeId());
                if (value instanceof List) {
                    List listOfTeamMates = (List) value;
                    String billableStatus = "NA";
                    for (Object obj : listOfTeamMates) {
                        ResourceVO projectTeamMate = (ResourceVO) obj;
                        String status = projectTeamMate.getBillableStatus();
                        if (status == null) {
                            status = "NA";
                        }
                        EmployeeDashboardVO empVo = new EmployeeDashboardVO();
                        BeanUtils.copyProperties(emp, empVo);
                        BeanUtils.copyProperties(projectTeamMate, empVo,
                                "employeeId", "employeeName", "emailId", "role",
                                "designation", "mobileNumber");
                        employeeDashboard.add(empVo);
                    }
                }
            } else {
                EmployeeDashboardVO empVo = new EmployeeDashboardVO();
                BeanUtils.copyProperties(emp, empVo);
                empVo.setBillableStatus("UA");
                empVo.setProjectAssigned(false);
                employeeDashboard.add(empVo);
            }
        }
        return employeeDashboard;
    }

//    @Override
//    public List<EmployeeDashboardVO> getDashBoardData() throws MyTeamException {
//        List<EmployeeDashboardVO> employeeDashboard = new ArrayList<>();
//        List<Employee> allEmployees = employeeService.getActiveEmployees();
//        List<ResourceVO> resources = resourceService.getAllResourcesVO();
//
//        for (Employee employee:allEmployees){
//            EmployeeDashboardVO employeeDashboardVO = new EmployeeDashboardVO();
//            BeanUtils.copyProperties(employee, employeeDashboardVO);
//            employeeDashboard.add(employeeDashboardVO);
//        }
//
//        employeeDashboard.stream().forEach(emp -> {
//            List<ResourceVO> latestResourceList = resources.stream().
//                    filter(res -> (res.getEmployeeId().equals(emp.getEmployeeId())&&res.getBillingEndDate().after(new Date()))).collect(Collectors.toList());
//            if(!latestResourceList.isEmpty()){
//                ResourceVO latestResource = latestResourceList.get(0);
//                Project resourceProject = projectService.getProjectByProjectId(latestResource.getProjectId());
//                if(!resourceProject.getStatus().equals("Completed")){
//                    emp.setProjectId(resourceProject.getProjectId());
//                    emp.setProjectName(resourceProject.getProjectName());
//                    emp.setBillingStartDate(latestResource.getBillingStartDate());
//                    emp.setBillingEndDate(latestResource.getBillingEndDate());
//                    Domain domain = domainService.getDomainById(resourceProject.getDomainId());
//                    Account account = accountService.getAccountById(resourceProject.getAccountId());
//                    emp.setAccountName(account.getAccountName());
//                    emp.setDomain(domain.getDomainName());
//                }
//            }
//        });
//        return employeeDashboard;
//    }
    
    @Override
    public List<EmployeeDashboardVO> getDashBoardData() throws MyTeamException {
        List<EmployeeDashboardVO> employeeDashboard = new ArrayList<>();
        List<Employee> allEmployees = employeeService.getActiveEmployees();
       // List<ResourceVO> resources = resourceService.getAllResourcesVO();

        for (Employee employee:allEmployees){
            EmployeeDashboardVO employeeDashboardVO = new EmployeeDashboardVO();
            EmployeeSubStatus subStatus = subStatusService.getCurrentSubStatus(employee.getEmployeeId());
            BeanUtils.copyProperties(employee, employeeDashboardVO);
            if(subStatus!=null)
                employeeDashboardVO.setEmpSubStatus(subStatus.getSubStatus());
            employeeDashboard.add(employeeDashboardVO);
        }

        employeeDashboard.stream().forEach(emp -> {
                Resource resource=resourceService.getCurrentAllocation(emp.getEmployeeId());
                		//resourceService.getLatestResourceByEmpId(emp.getEmployeeId());
           
                Project resourceProject = projectService.getProjectByProjectId(resource.getProjectId());
                if(!resourceProject.getStatus().equals("Completed")){
                    emp.setProjectId(resourceProject.getProjectId());
                	emp.setBillableStatus(resource.getBillableStatus());
                	emp.setOnBehalfOf(resource.getOnBehalfOf()!=null?employeeService.getEmployeeById(resource.getOnBehalfOf()).getEmployeeName():"");
                    emp.setProjectName(resourceProject.getProjectName());
                    emp.setBillingStartDate(resource.getBillingStartDate());
                    emp.setBillingEndDate(resource.getBillingEndDate());
                    Domain domain = domainService.getDomainById(resourceProject.getDomainId());
                    Account account = accountService.getAccountById(resourceProject.getAccountId());
                    emp.setAccountName(account.getAccountName());
                    emp.setDomain(domain.getDomainName());
                }
      
        });
        return employeeDashboard;
    }
    
    
}
