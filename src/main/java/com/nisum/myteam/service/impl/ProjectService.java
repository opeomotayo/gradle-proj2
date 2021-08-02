package com.nisum.myteam.service.impl;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.*;
import com.nisum.myteam.repository.ProjectRepo;
import com.nisum.myteam.service.*;
import com.nisum.myteam.utils.MyTeamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("projectService")
@Slf4j
public class ProjectService implements IProjectService {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private EmployeeDataService employeeDataBaseService;

    @Autowired
    private PdfReportGenerator pdfReportGenerator;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IDomainService domainService;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private IAccountService accountService;


    @Override
    public Project createProject(Project project, String loginEmpId) throws MyTeamException {
        if (project.getDomainId() == null) {
            //List<Domain> domainsList = domainRepo.findByAccountId(project.getAccountId());
            List<Domain> domainsList = domainService.getDomainsUnderAccount(project.getAccountId());
            Domain savedDomain = domainsList.get(0);
            project.setDomainId(savedDomain.getDomainId());
            //project.setDomain(savedDomain.getDomainName());
        }
        project.setAuditFields(loginEmpId, MyTeamUtils.CREATE);
        return projectRepo.save(project);
    }

    @Override
    public Project updateProject(Project project, String loginEmpId) throws MyTeamException {
        Project existingProject = projectRepo.findByProjectId(project.getProjectId());
        if (project.getProjectEndDate().compareTo(new Date()) <= 0
                && project.getProjectEndDate().compareTo(existingProject.getProjectEndDate()) != 0) {

            existingProject.setStatus(MyTeamUtils.IN_ACTIVE);
            existingProject.setProjectEndDate(project.getProjectEndDate());
            existingProject.setAuditFields(loginEmpId, MyTeamUtils.UPDATE);
            projectRepo.save(existingProject);
        }else{
            project.setId(existingProject.getId());
            projectRepo.save(project);
        }
        return existingProject;
    }

    public Account updateProjSeqinAccount(Account account) throws MyTeamException {
        return accountService.updateAccountSequence(account);
    }

    @Override
    public void deleteProject(String projectId) {
        Project project = projectRepo.findByProjectId(projectId);
        projectRepo.delete(project);

        resourceService.deleteResourcesUnderProject(projectId);
    }

    public boolean isProjectExistsByName(String projectName) {
        boolean isProjectExists = false;
        if (projectName != null && !"".equalsIgnoreCase(projectName)) {
            Project project = projectRepo.findByProjectName(projectName);
            isProjectExists = (project == null) ? false : true;
        }
        return isProjectExists;
    }

    public boolean isProjectExistsById(String projectId) {
        boolean isProjectExists = false;
        if (projectId != null && !"".equalsIgnoreCase(projectId)) {
            Project project = projectRepo.findByProjectId(projectId);
            isProjectExists = (project == null) ? false : true;
        }
        return isProjectExists;
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public long getProjectsCount() {
        return projectRepo.count();

    }

    public Project getProjectByProjectId(String projectId) {
        Project project = null;
        if (StringUtils.isNotBlank(projectId)) {
            project = projectRepo.findByProjectId(projectId);

        }
        return project;
    }

    public List<Project> getActiveProjects() throws MyTeamException {
        List<Project> projects = projectRepo.findAll();
        return projects.stream().filter(project -> project.getStatus().equalsIgnoreCase(MyTeamUtils.ACTIVE)).collect(Collectors.toList());

    }

    public List<Project> getProjectsForDeliveryLead(String deliveryLeadId) {
        List<Project> projectsList = projectRepo.findByDeliveryLeadIds(deliveryLeadId);
        return projectsList;
    }

    @Override
    public List<Project> getProjectsUnderDomain(String domainId) {
        return projectRepo.findByDomainId(domainId);
    }

    public Account getProjectAccount(String accountId) {
        Account account = null;
        if (accountId != null && !"".equalsIgnoreCase(accountId)) {
            return accountService.getAccountById(accountId);
        }
        return account;
    }


    @Override
    public List<HashMap<Object, Object>> getProjects() throws MyTeamException {
        /*
         * MT-79:returns all project info. This will get implemented once
         * managers are maintained as List and not attributes return
         * projectRepo.findAll();
         */
        List<HashMap<Object, Object>> projectList = new ArrayList<>();
        List<HashMap<String, String>> EmployeeList = null;
        List<HashMap<String, String>> managerList = null;
        List<Project> projects = projectRepo.findAll();

        for (Project project : projects) {
            HashMap<Object, Object> projectMap = new HashMap<>();
            projectMap.put("id", project.getId());
            projectMap.put("projectId", project.getProjectId());
            projectMap.put("projectName", project.getProjectName());


            Account account = accountService.getAccountById(project.getAccountId());

            //Domain domain = domainRepo.findByDomainId(project.getDomainId());
            Domain domain = domainService.getDomainById(project.getDomainId());
            if (domain != null)
                projectMap.put("domain", domain.getDomainName());
            if (account != null)
                projectMap.put("account", account.getAccountName());
            projectMap.put("accountId", project.getAccountId());
            projectMap.put("domainId", project.getDomainId());
            projectMap.put("status", project.getStatus());
            projectMap.put("employeeIds", project.getEmployeeIds());
            projectMap.put("projectStartDate", project.getProjectStartDate());
            projectMap.put("projectEndDate", project.getProjectEndDate());

            if (project.getDeliveryLeadIds() != null) {

                List<String> deliverLeadsId = project.getDeliveryLeadIds();
                EmployeeList = employeeService.getDeliveryManagerMap(deliverLeadsId);
                projectMap.put("deliveryLeadIds", EmployeeList);
            }
            if (project.getManagerIds() != null) {

                List<String> managerid = project.getManagerIds();
                managerList = employeeService.getDeliveryManagerMap(managerid);
                projectMap.put("managerIds", managerList);
            }
            projectList.add(projectMap);
        }
        return projectList;
    }


    @Override
    public List<EmpLoginData> employeeLoginsBasedOnDate(long id,
                                                        String fromDate, String toDate) throws MyTeamException {
        return employeeDataBaseService.fetchEmployeeLoginsBasedOnDates(id, fromDate, toDate);
    }

    @Override
    public String generatePdfReport(long id, String fromDate, String toDate) throws MyTeamException {
        return pdfReportGenerator.generateeReport(id, fromDate, toDate);
    }

    @Override
    public List<Resource> getResourcesUnderProject(String empId) {
        List<String> projectsIdsList = new ArrayList<>();
        List<Resource> resourcesList = new ArrayList<>();

        List<Project> projectsList = projectRepo.findByDeliveryLeadIds(empId);
        for (Project project : projectsList)
            projectsIdsList.add(project.getProjectId());

        Query query = new Query(Criteria.where("projectId").in(projectsIdsList));
        List<Resource> resourcesListPersisted = mongoTemplate.find(query, Resource.class);


        for (Resource resource : resourcesListPersisted) {
            if (!resource.getEmployeeId().equals(empId))
                resourcesList.add(resource);
        }
        return resourcesList;

    }

    @Override
    public List<Project> getProjectsByAccountId(String accId) {
        return projectRepo.findByAccountId(accId);
    }




    @Override
    public List<Project> getProjectsUnderDeliveryLead(String deliveryLeadId) throws MyTeamException {

        List<Project> projectList = new ArrayList<>();
        List<Project> projectListPersisted = projectRepo.findByDeliveryLeadIds(deliveryLeadId);
        for (Project project : projectListPersisted) {
            if (!project.getStatus().equals("Completed")) {
                projectList.add(project);
            }

        }
        return projectList;
    }

    @Override
    public List<Project> getOnlyActiveProjects() {
        return getAllProjects().stream()
                .filter(project -> !project.getStatus().equalsIgnoreCase("Completed"))
                .collect(Collectors.toList());
    }

    private String validateAgainstDOJ(Resource resource) {
        String response = null;
        Date empDoj = employeeService.getEmployeeById(resource.getEmployeeId()).getDateOfJoining();
        if (resource.getBillingStartDate().compareTo(empDoj) < 0) {
            response = "Resource Start Date (" + resource.getBillingStartDate() + " ) in " + getProjectByProjectId(resource.getProjectId()).getProjectName()
                    + " project should not be before Date of Joining ( " + empDoj + ").";
        }
        return response;
    }

    @Override
    public Set<String> accountsAssignedToDl(String empId) {
        Set<String> accIdsSet = new HashSet<String>();

        List<Project> prjtsList = projectRepo.findByDeliveryLeadIds(empId);

        if (null != prjtsList && !prjtsList.isEmpty() && MyTeamUtils.INT_ZERO < prjtsList.size()) {
            for (Project obj : prjtsList) {
                accIdsSet.add(obj.getAccountId());
            }
        }
        return accIdsSet;
    }


    @Override
    public List<HashMap<Object, Object>> deliveryLeadProjects(String empId) throws MyTeamException {
        List<HashMap<Object, Object>> projectsList = new ArrayList<HashMap<Object, Object>>();

        Set<String> accIdsSet = domainService.accountsAssignedToDeliveryLead(empId);
        List<Project> prjts = projectRepo.findByAccountIdIn(accIdsSet);

        if (null != prjts && !prjts.isEmpty() && MyTeamUtils.INT_ZERO < prjts.size()) {
            Account account = null;
            Domain domain = null;

            HashMap<Object, Object> projectMap = null;

            for (Project obj : prjts) {
                projectMap = new HashMap<>();

                account = accountService.getAccountById(obj.getAccountId());
                domain = domainService.getDomainById(obj.getDomainId());

                projectMap.put("projectId", obj.getProjectId());
                projectMap.put("projectName", obj.getProjectName());

                projectMap.put("accountId", obj.getAccountId());
                projectMap.put("domainId", obj.getDomainId());

                projectMap.put("account", null != account ? account.getAccountName() : "");
                projectMap.put("domain", null != domain ? domain.getDomainName() : "");
                projectMap.put("status", obj.getStatus());


                projectMap.put("deliveryLeadIds", null != obj.getDeliveryLeadIds() ? employeeService.getDeliveryManagerMap(obj.getDeliveryLeadIds()) : "");
                projectMap.put("managerIds", null != obj.getManagerIds() ? employeeService.getDeliveryManagerMap(obj.getManagerIds()) : "");

                projectsList.add(projectMap);
            }
        }
        return projectsList;
    }


   /*

   Need to refactor the following code
    */

//    @Override
//    public List<HashMap<Object, Object>> getRoleBasedProjects(String empId) throws MyTeamException {
//        List<HashMap<Object, Object>> projectsList = new ArrayList<HashMap<Object, Object>>();
//        Set<String> accountIdSet = domainService.accountsAssignedToDeliveryLead(empId);
//        List<Project> projectList = projectRepo.findByAccountIdIn(accountIdSet);
//
//        for (Project proj : projectList) {
//        	if(proj.getDeliveryLeadIds().stream().anyMatch(e->e.equals(empId)))
//                 addToProjectList(projectsList, proj);
//        }
//        return projectsList;
//    }
    @Override
    public List<HashMap<Object, Object>> getRoleBasedProjects(String empId) throws MyTeamException {
        List<HashMap<Object, Object>> projectsList = new ArrayList<HashMap<Object, Object>>();
        Set<String> accountIdSet = domainService.accountsAssignedToDeliveryLead(empId);
        List<Project> projectList = projectRepo.findByAccountIdIn(accountIdSet);

        for (Project proj : projectList) {
                addToProjectList(projectsList, proj);
        }
        return projectsList;
    }

    @Override
    public List<HashMap<Object, Object>> getProjectsInsteadOfRole() throws MyTeamException {
        List<HashMap<Object, Object>> projectList = new ArrayList<>();
        List<Project> projects = projectRepo.findAll();
        for (Project proj : projects) {
            addToProjectList(projectList, proj);
        }
        return projectList;
    }


    private void addToProjectList(List<HashMap<Object, Object>> projectList, Project proj) {
        HashMap<Object, Object> projectMap = new HashMap<>();
        buildProjectProperties(proj, projectMap);
        projectMap.put("status", proj.getStatus());
        projectMap.put("projectStartDate", proj.getProjectStartDate());
        projectMap.put("projectEndDate", proj.getProjectEndDate());
        projectList.add(projectMap);
    }

    private void buildProjectProperties(Project proj, HashMap<Object, Object> projectMap) {
        projectMap.put("id", proj.getId());
        projectMap.put("projectId", proj.getProjectId());
        projectMap.put("projectName", proj.getProjectName());

        Account account = accountService.getAccountById(proj.getAccountId());
        Domain domain = domainService.getDomainById(proj.getDomainId());

        if (domain != null)
            projectMap.put("domain", domain.getDomainName());
        if (account != null)
            projectMap.put("account", account.getAccountName());
        projectMap.put("accountId", proj.getAccountId());
        projectMap.put("domainId", proj.getDomainId());
        projectMap.put("employeeIds", proj.getEmployeeIds());
        if (proj.getDeliveryLeadIds() != null) {
            //employeeService.getDeliveryManagerMap(proj.getDeliveryLeadIds()).stream().map(mapObj->mapObj.get("employeeId")).collect(Collectors.toList());
            projectMap.put("deliveryLeadIds", employeeService.getDeliveryManagerMap(proj.getDeliveryLeadIds()));
        }
        if (proj.getManagerIds() != null) {
            projectMap.put("managerIds", employeeService.getDeliveryManagerMap(proj.getManagerIds()));

        }

    }



}



//    public Resource addNewBeanchAllocation(Employee employee, String loginEmpId) {
//        Resource resourcePersisted = null;
//        Resource resourceBench = new Resource();
//        resourceBench.setAccount(MyTeamUtils.BENCH_ACCOUNT);
//        resourceBench.setBillableStatus(MyTeamUtils.BENCH_BILLABILITY_STATUS);
//        resourceBench.setDesignation(employee.getDesignation());
//        resourceBench.setEmailId(employee.getEmailId());
//        resourceBench.setEmployeeId(employee.getEmployeeId());
//        resourceBench.setMobileNumber(employee.getMobileNumber());
//
//        resourceBench.setEmployeeName(employee.getEmployeeName());
//        resourceBench.setProjectId(MyTeamUtils.BENCH_PROJECT_ID);
//        resourceBench.setStartDate(employee.getDateOfJoining() != null ? employee.getDateOfJoining() : new Date());
//
//        Project project = projectRepo.findByProjectId(MyTeamUtils.BENCH_PROJECT_ID);
//        resourceBench.setProjectName(project.getProjectName());
//        resourceBench.setAccountId(project.getAccountId());
//        resourceBench.setDomainId(project.getDomainId());
//        resourceBench.setShift(employee.getShift());
//        resourceBench.setRole(employee.getRole());
//
//        if (null != employee.getEmpStatus() && employee.getEmpStatus().trim().equalsIgnoreCase(MyTeamUtils.IN_ACTIVE_SPACE)) {
//            resourceBench.setEndDate(employee.getEndDate());
//            resourceBench.setActive(false);
//        } else {
//            employee.setEmpStatus(MyTeamUtils.ACTIVE);
//            resourceBench.setEndDate(project.getProjectEndDate());
//            resourceBench.setActive(true);
//        }
//
//        try {
//            resourcePersisted = resourceService.addResource(resourceBench, loginEmpId);
//        } catch (MyTeamException e) {
//            e.printStackTrace();
//        }
//
//        return resourcePersisted;
//    }




//List<Resource> existingTeammates = resourceRepo.findByProjectId(project.getProjectId());
//            for (Resource existingTeammate : existingTeammates) {
//                existingTeammate.setActive(false);
//                existingTeammate.setEndDate(project.getProjectEndDate());
//                existingTeammate.setAuditFields(loginEmpId, MyTeamUtils.UPDATE);
//                resourceRepo.save(existingTeammate);

//                Billing billingDetails = new Billing();
//                Resource newBenchAllocation = new Resource();
//                billingDetails.setBillableStatus(MyTeamUtils.BENCH_BILLABILITY_STATUS);
//                List<Billing> listBD = billingService.getActiveBillings(existingTeammate.getEmployeeId(),
//                        existingTeammate.getProjectId());
//                if (listBD != null && !listBD.isEmpty()) {
//                    Billing billingDetailsExisting = listBD.get(0);
//                    billingDetailsExisting.setBillingEndDate(project.getProjectEndDate());
//                    billingDetailsExisting.setActive(false);
//                    billingService.updateBilling(billingDetailsExisting, loginEmpId);
//                }
//                Project benchProject = projectRepo.findByProjectId(MyTeamUtils.BENCH_PROJECT_ID);
//                Date sd = project.getProjectEndDate();
//                billingDetails.setBillingStartDate(sd);
//                billingDetails.setAccount(MyTeamUtils.BENCH_ACCOUNT);
//                billingDetails.setActive(true);
//                billingDetails.setEmployeeId(existingTeammate.getEmployeeId());
//                billingDetails.setEmployeeName(existingTeammate.getEmployeeName());
//                //billingDetails.setCreateDate(new Date());// Commented as added common audit fields
//                billingDetails.setProjectId(MyTeamUtils.BENCH_PROJECT_ID);
//                billingDetails.setProjectName(MyTeamUtils.FREE_POLL);
//                if (benchProject != null) {
//                    billingDetails.setBillingEndDate(benchProject.getProjectEndDate());
//                    newBenchAllocation.setAccountId(benchProject.getAccountId());
//                    newBenchAllocation.setProjectName(benchProject.getProjectName());
//                    newBenchAllocation.setDomainId(benchProject.getDomainId());
//                    newBenchAllocation.setEndDate(benchProject.getProjectEndDate());
//                }

//  billingService.addBilling(billingDetails, loginEmpId);

//                newBenchAllocation.setRole(existingTeammate.getRole());
//                newBenchAllocation.setAccount(MyTeamUtils.BENCH_ACCOUNT);
//                newBenchAllocation.setShift(existingTeammate.getShift());
//                newBenchAllocation.setBillableStatus(MyTeamUtils.BENCH_BILLABILITY_STATUS);
//                newBenchAllocation.setDesignation(existingTeammate.getDesignation());
//                newBenchAllocation.setEmailId(existingTeammate.getEmailId());
//                newBenchAllocation.setEmployeeId(existingTeammate.getEmployeeId());
//                newBenchAllocation.setActive(true);
//                newBenchAllocation.setEmployeeName(existingTeammate.getEmployeeName());
//                newBenchAllocation.setProjectId(MyTeamUtils.BENCH_PROJECT_ID);
//                newBenchAllocation.setStartDate(sd);
//                newBenchAllocation.setAuditFields(loginEmpId, MyTeamUtils.CREATE);
//                resourceRepo.save(newBenchAllocation);

//empShiftService.updateEmployeeShift(existingTeammate, loginEmpId);
// }


//        else {
//            Query query = new Query(
//                    Criteria.where("projectId").is(project.getProjectId()));
//            Update update = new Update();
//            update.set("projectName", project.getProjectName());
//            if (project.getDeliveryLeadIds() != null) {
//                update.set("deliveryLeadIds", project.getDeliveryLeadIds());
//            }
//
//            update.set("domain", domainService.getDomainById(project.getDomainId()).getDomainName());
//
//            update.set("domainId", project.getDomainId());
//            update.set("accountId", project.getAccountId());
//            update.set("status", project.getStatus());
//            update.set("projectStartDate", project.getProjectStartDate());
//            update.set("projectEndDate", project.getProjectEndDate());
//            FindAndModifyOptions options = new FindAndModifyOptions();
//            //Setting audit fileds
//            update.set("modifiedBy", loginEmpId);
//            update.set("lastModifiedOn", new Date());
//
//            options.returnNew(true);
//            options.upsert(true);
//            Project projectDB = mongoTemplate.findAndModify(query, update, options, Project.class);
//            List<Resource> employeeDetails = resourceRepo.findByProjectId(project.getProjectId());
////            if (employeeDetails != null && projectDB != null) {
////                for (Resource teamMate : employeeDetails) {
////                    teamMate.setAccountId(projectDB.getAccountId());
////                    teamMate.setProjectName(projectDB.getProjectName());
////                    teamMate.setEndDate(projectDB.getProjectEndDate());
////                    teamMate.setAuditFields(loginEmpId, MyTeamUtils.UPDATE);//Setting audit fields
////                    resourceRepo.save(teamMate);
////                }
////            }
//            return projectDB;
//        }






