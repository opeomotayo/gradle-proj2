
package com.nisum.myteam.service.impl;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.*;
import com.nisum.myteam.model.vo.*;
import com.nisum.myteam.repository.ResourceRepo;
import com.nisum.myteam.service.*;
import com.nisum.myteam.statuscodes.ResourceAllocationStatus;
import com.nisum.myteam.statuscodes.ResourceStatus;
import com.nisum.myteam.utils.MyTeamDateUtils;
import com.nisum.myteam.utils.MyTeamUtils;
import com.nisum.myteam.utils.constants.Shifts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ResourceService implements IResourceService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ResourceRepo resourceRepo;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IDomainService domainService;

    @Autowired
    private IProjectService projectService;

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private IEmployeeShiftService empShiftService;

    public HashMap<String, Object> respMap = new HashMap<>();


    private Resource getLatestAllocation(List<Resource> resourceAllocList) {
        Resource latestAlloc = null;
        if (!resourceAllocList.isEmpty()) {
            latestAlloc = resourceAllocList.get(0);
            for (Resource resource : resourceAllocList) {
                if (latestAlloc.getBillingEndDate().before(resource.getBillingEndDate()))
                    latestAlloc = resource;
            }
        }
        return latestAlloc;
    }
    public Resource getFirstOfListOrNull(List<Resource> resourceList){
        if(resourceList != null && !resourceList.isEmpty()){
            return resourceList.get(0);
        }else {
            return null;
        }
    }

    public List<Resource> getResourcesByEmployeeId(String employeeId) {
        return resourceRepo.findByEmployeeId(employeeId);

    }

    public Resource addResource(Resource resourceReq, String loginEmpId) throws MyTeamException {

        Resource currentAllocation = getCurrentAllocationIfNotReturnNull(resourceReq.getEmployeeId());
        Resource resourcePers = null;
       

        if(currentAllocation != null && currentAllocation.getProjectId().equals(MyTeamUtils.BENCH_PROJECT_ID)){
            currentAllocation.setBillingEndDate(MyTeamDateUtils.getDayLessThanDate(resourceReq.getBillingStartDate()));
            if(currentAllocation.getBillingStartDate().compareTo(currentAllocation.getBillingEndDate())<=0)
                this.updateExistedResource(currentAllocation);                  //updateLatestProjectAllocationToEnd
            else
            	resourceRepo.delete(currentAllocation);
            resourcePers = resourceRepo.save(resourceReq);               //createNewProjectAllocationtoStart
            respMap.put("message","Resource has been created");  // added on 21-7 2019
        }else{
            resourcePers = resourceRepo.save(resourceReq);
            respMap.put("message","Resource has been created");
        }
        return resourcePers;
    }

    public boolean isResourceExistsForProject(String employeeId, String projectId) {
        boolean isExists = false;

        List<Resource> resourceList = resourceRepo.findByEmployeeIdAndProjectId(employeeId, projectId);
        if (resourceList != null && resourceList.size() > 0) {
            isExists = true;
            respMap.put("message", "Resourse is already in the project");
            return isExists;
        }
        respMap.put("statusCode", 810);
        respMap.put("message", "Resource Not Found");
        return isExists;
    }


    public void updateResourceDetails(Resource resourceReq, String loginEmpId) throws MyTeamException {

//        List<Resource> resourceAllocList = resourceRepo.findByEmployeeIdAndProjectId(resourceReq.getEmployeeId(), resourceReq.getProjectId());
//
//        List<Resource> resourceListWithLatestRecord = resourceAllocList.stream().filter(resource -> resource.getBillingEndDate().compareTo(new Date()) >= 0).collect(Collectors.toList());
//
//        if (resourceListWithLatestRecord != null && resourceListWithLatestRecord.size() > 0) {
//
//            Resource resourcePrev = resourceListWithLatestRecord.get(0);                                     //latest resource record.
//            log.info("Requsting Resource Allocation BillingStart Date::::"+resourceReq.getBillingStartDate());
//            log.info("The before date is::" + MyTeamDateUtils.getDayLessThanDate(resourceReq.getBillingStartDate()));
//            if(!resourcePrev.getBillableStatus().equals(resourceReq.getBillableStatus())) {
//                if (resourcePrev.getBillingEndDate().compareTo(new Date()) == 0) {
//                    resourcePrev.setBillingEndDate(new Date());
//                } else {
//                    resourcePrev.setBillingEndDate(MyTeamDateUtils.getDayLessThanDate(resourceReq.getBillingStartDate())); //adding resource.
//                }
//                insertNewResourceWithNewStatus(resourceReq,loginEmpId);
//            }else {
//                resourcePrev.setResourceRole(resourceReq.getResourceRole());
//                resourcePrev.setBillingStartDate(resourceReq.getBillingStartDate());
//                resourcePrev.setBillingEndDate(resourceReq.getBillingEndDate());
//                //resourceAllocPrev.setBillingEndDate(); //adding resource.
//            }
//            log.info("After setting the date:::before saving the Resource::" + resourcePrev);
//            this.updateExistedResource(resourcePrev);
//        }
        Resource resource = resourceRepo.findById(resourceReq.getId());
        if (resource != null) {

            if(!resource.getStatus().equalsIgnoreCase(MyTeamUtils.STATUS_RELEASED) || resourceReq.getProjectId().equalsIgnoreCase(MyTeamUtils.BENCH_PROJECT_ID)){

                if(resource.getStatus().equals(MyTeamUtils.STATUS_PROPOSED) && resourceReq.getStatus().equals(MyTeamUtils.STATUS_ENGAGED)){
                    if(validateResourceForProposedToEngage(resourceReq)){
                        this.updateExistedResource(resourceReq);
//                    return;
                    }
                }else {
                    if (isDatesAvailableForAllocation(resourceReq)) {
                        Resource prevAllocation = getFirstOfListOrNull(resourceRepo.findByEmployeeId(resourceReq.getEmployeeId()).stream().
                                filter(a -> a.getBillingEndDate().compareTo(MyTeamDateUtils.getDayLessThanDate(resource.getBillingStartDate())) == 0).collect(Collectors.toList()));
                        Resource nextAllocation = getFirstOfListOrNull(resourceRepo.findByEmployeeId(resourceReq.getEmployeeId()).stream().filter(a ->
                                a.getBillingStartDate().compareTo(MyTeamDateUtils.getDayMoreThanDate(resource.getBillingEndDate())) == 0).collect(Collectors.toList()));
                        if (prevAllocation != null && prevAllocation.getProjectId().equalsIgnoreCase(MyTeamUtils.BENCH_PROJECT_ID)) {
                            prevAllocation.setBillingEndDate(MyTeamDateUtils.getDayLessThanDate(resourceReq.getBillingStartDate()));
                            this.updateExistedResource(prevAllocation);
                        }
                        if (nextAllocation != null && nextAllocation.getProjectId().equalsIgnoreCase(MyTeamUtils.BENCH_PROJECT_ID)) {
                            nextAllocation.setBillingStartDate(MyTeamDateUtils.getDayMoreThanDate(resourceReq.getBillingEndDate()));
                            this.updateExistedResource(nextAllocation);
                        }
                        resource.setOnBehalfOf(resourceReq.getOnBehalfOf());
                        resource.setBillableStatus(resourceReq.getBillableStatus());
                        resource.setBillingStartDate(resourceReq.getBillingStartDate());
                        resource.setBillingEndDate(resourceReq.getBillingEndDate());
                        resource.setResourceRole(resourceReq.getResourceRole());
                        resource.setAuditFields(loginEmpId,MyTeamUtils.UPDATE);
                        this.updateExistedResource(resource);

                    }
                }
            }else{
                respMap.put("statusCode", 811);
                respMap.put("message", "Resource is already released from you, And you can't update this allocation");
            }

        } else {
            respMap.put("statusCode", 811);
            respMap.put("message", "Record Not Found");
        }

    }

    public void updateExistedResource(Resource resource) {

        if (resource != null) {
            Resource resourcePers = resourceRepo.save(resource);
            respMap.put("statusCode", 801);
            respMap.put("message", "Resource updated successfully");
            respMap.put("resourceObj", resourcePers);

        }

    }

    public void insertNewResourceWithNewStatus(Resource resourceReq, String loginEmpId) throws MyTeamException {

        resourceReq.setId(null);
        Resource resourcePers = resourceRepo.insert(resourceReq);
        respMap.put("statusCode", 801);
        respMap.put("message", "Resource updated successfully");
        respMap.put("resourceObj", resourcePers);

    }

    public boolean validateAllocationAgainstPrevAllocation(Resource resourceReq) {
        boolean isValid = true;
        List<Resource> resourceAllocList = resourceRepo.findByEmployeeIdAndProjectId(resourceReq.getEmployeeId(), resourceReq.getProjectId());
        Resource prevAllocation = this.getLatestAllocation(resourceAllocList);
        if (prevAllocation != null) {
            if (!prevAllocation.getBillingEndDate().before(resourceReq.getBillingStartDate())) {
                respMap.put("statusCode", 811);
                respMap.put("message", "Billing start date should be after previous allocation billing end date in this project");
                isValid = false;
            }
        }
        return isValid;
    }

    public boolean validateBillingStartEndDateAgainstProjectStartEndDate(Resource resource, String loginEmpId) throws MyTeamException {

        boolean isValid = true;

        Project project = projectService.getProjectByProjectId(resource.getProjectId());
        log.info("Project::" + project);

        if (!(resource.getBillingStartDate().compareTo(project.getProjectStartDate())>=0)) {
            log.info("Billing start date should be after Project start date");
            respMap.put("statusCode", 811);
            respMap.put("message", "Billing start date should be after Project start date");
            isValid = false;
        }

        if (!(resource.getBillingStartDate().compareTo(resource.getBillingEndDate())<=0)) {
            log.info("Billing start date should be before Billing End Date.");
            respMap.put("statusCode", 812);
            respMap.put("message", "Billing start date should be before Billing End Date.");
            isValid = false;
        }
        log.info("ResourceALloc Req::" + resource);
        log.info("" + project.getProjectEndDate().toString());

        //if (!resourceAllocation.getBillingEndDate().before(project.getProjectEndDate())|| !resourceAllocation.getBillingEndDate().equals(project.getProjectEndDate())) {
        if (!(resource.getBillingEndDate().compareTo(project.getProjectEndDate()) <= 0)) {
            log.info("Billing end date should be on or before Project End Date.");
            respMap.put("statusCode", 813);
            respMap.put("message", "Billing end date should be before Project End Date.");
            isValid = false;
        }

        respMap.put("resourceObj", resource);
        return isValid;
    }


    public boolean validateBillingStartDateAgainstDOJ(Resource resource) {
        String message = "";
        boolean isValid = true;

        Employee employee = employeeService.getEmployeeById(resource.getEmployeeId());
        Date empDoj = employeeService.getEmployeeById(resource.getEmployeeId()).getDateOfJoining();
        if (resource.getBillingStartDate().compareTo(empDoj) < 0) {
            message = "Resource Billing Start Date (" + MyTeamDateUtils.getRadableDate().format(resource.getBillingStartDate()) + " ) in "
                    + projectService.getProjectByProjectId(resource.getProjectId()).getProjectName()
                    + " project should not be before Date of Joining ( " + MyTeamDateUtils.getRadableDate().format(empDoj) + ").";
            isValid = false;
            respMap.put("statusCode", 814);
            respMap.put("message", message);
        }
        return isValid;
    }
    
	private boolean isDateCommon(Resource e, Date fromDate, Date toDate) {
		for (LocalDate datei = convert(fromDate); datei.isBefore(convert(toDate).plusDays(1)); datei = datei.plusDays(1))
			for (LocalDate datej = convert(e.getBillingStartDate()); datej.isBefore(convert(e.getBillingEndDate()).plusDays(1)); datej = datej.plusDays(1))
			   if(datej.equals(datei))
				   return true;
		return false;
	}


	private LocalDate convert(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime());
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		return localDateTime.toLocalDate();
		
	}
	
    public boolean isDatesAvailableForAllocation(Resource resource){
        String message = "";
        List<Resource> allocationList = resourceRepo.findByEmployeeId(resource.getEmployeeId());

        List<Resource> matchedList = allocationList.stream().filter(r -> !r.getProjectId().equals(MyTeamUtils.BENCH_PROJECT_ID) &&
                !r.getId().equals(resource.getId()) && isDateCommon(resource,r.getBillingStartDate(),r.getBillingEndDate())
                  )
                .collect(Collectors.toList());
        if(!matchedList.isEmpty()){
            message = "Resource is already alocated for projects:\n";
            for(Resource resourcel:matchedList){
                Project project = projectService.getProjectByProjectId(resourcel.getProjectId());
                message += "Project:"+project.getProjectName()+" From:"+MyTeamDateUtils.getRadableDate().format(resourcel.getBillingStartDate())+" To:"
                        +MyTeamDateUtils.getRadableDate().format(resourcel.getBillingEndDate())+"\n";
            }
            respMap.put("statusCode", 815);
            respMap.put("message", message);
            return false;
        }else
            return true;

    }


    public boolean isResourceAvailable(Resource resourceReq) {

        boolean isAssigned = true;
        String message = "";
        List<Resource> resourceAllocList = resourceRepo.findByEmployeeId(resourceReq.getEmployeeId()); //getting all allocations of employee
        Resource latestAllocation = getLatestAllocation(resourceAllocList);
        if(latestAllocation != null && !isAllocationActiveToday(resourceReq) && !latestAllocation.getProjectId().equals(MyTeamUtils.BENCH_PROJECT_ID) &&
                latestAllocation.getBillingStartDate().compareTo(new Date())>0){
            isAssigned = false;
            Project project = projectService.getProjectByProjectId(latestAllocation.getProjectId());
            message = "Resource is already Reserved in "+project.getProjectName()+" project from "+
                    MyTeamDateUtils.getRadableDate().format(latestAllocation.getBillingStartDate())+" to "+MyTeamDateUtils.getRadableDate().format(latestAllocation.getBillingEndDate());

        }else if(!isDatesAvailableForAllocation(resourceReq)){
            message = respMap.get("message").toString();
            isAssigned = false;
        }else if(!isReleased(resourceReq)){
            message = respMap.get("message").toString();
            isAssigned = false;
        }
        respMap.put("statusCode", 815);
        respMap.put("message", message);
        return isAssigned;
    }

    public boolean validateResourceForProposedToEngage(Resource resourceReq){
        String message = "";
        boolean isValid = false;
        Resource resource = resourceRepo.findById(resourceReq.getId());
        if(resource != null){
            List<Resource> resourceList = resourceRepo.findByEmployeeId(resourceReq.getEmployeeId());
            Resource lastAllocation = this.getLatestAllocation(resourceList.stream().filter(a -> a.getBillingEndDate().compareTo(resource.getBillingStartDate())<0 &&
                    !a.getProjectId().equalsIgnoreCase(MyTeamUtils.BENCH_PROJECT_ID)).collect(Collectors.toList()));

            if(lastAllocation != null && !lastAllocation.getStatus().equals(MyTeamUtils.STATUS_RELEASED)){
                message = "Resource is not released form last allocation";
            }else{
                message = "Resource updated successfully";
                isValid = true;
            }


            respMap.put("statusCode", 810);
            respMap.put("message", message);
        }

        return isValid;
    }

    public boolean isReleased(Resource resourceReq){
        String message = "";
        boolean released = true;
        List<Resource> resourceList = resourceRepo.findByEmployeeId(resourceReq.getEmployeeId());
        Resource currentAllocation = getCurrentAllocationIfNotReturnNull(resourceReq.getEmployeeId());
        if(currentAllocation == null){
            Resource latestAllocation = getLatestAllocation(resourceList.stream().filter(r -> r.getBillingEndDate().compareTo(resourceReq.getBillingStartDate()) < 0).collect(Collectors.toList()));
            if(!resourceReq.getStatus().equals(MyTeamUtils.STATUS_PROPOSED) && latestAllocation != null && latestAllocation.getStatus().equals(MyTeamUtils.STATUS_ENGAGED)){
                //not released
            	released=false;
                message = "Resource is not released from "+projectService.getProjectByProjectId(latestAllocation.getProjectId()).getProjectName();
                respMap.put("statusCode", 810);
            }
        }
        respMap.put("message", message);

        return released;
    }

    public boolean validateResourceBillingEndDateAgainstBench(Resource resourceReq){
        boolean isValid = true;
        String message = "";

        List<Resource> resourceAllocList = resourceRepo.findByEmployeeIdAndProjectId(resourceReq.getEmployeeId(),MyTeamUtils.BENCH_PROJECT_ID);

        Resource resourceBenchLatestRecord = getLatestAllocation(resourceAllocList.stream().
                filter(r -> r.getProjectId().equalsIgnoreCase(MyTeamUtils.BENCH_PROJECT_ID)).collect(Collectors.toList()));

        if(!isAllocationActiveToday(resourceBenchLatestRecord)){
            isValid = false;
            message = "Resource is not available for allocation";

        }else if(!(resourceReq.getBillingEndDate().before(resourceBenchLatestRecord.getBillingEndDate()) &&
                resourceReq.getBillingStartDate().after(resourceBenchLatestRecord.getBillingStartDate()))){
            message = "Resource is available from "+resourceBenchLatestRecord.getBillingStartDate()+" to "+resourceBenchLatestRecord.getBillingEndDate();
            isValid = false;
        }
        respMap.put("statusCode", 810);
        respMap.put("message", message);
        return isValid;
    }

    public boolean isAllocationActiveToday(Resource resource){
        boolean isActive = true;
        if(resource.getBillingStartDate().compareTo(new Date()) <=0 &&
                resource.getBillingEndDate().compareTo(new Date())>=0){
            isActive = true;
        }else{
            isActive = false;
        }
        return isActive;
    }

    public Resource getAllocationOfDate(String employeeId,Date onDate){
        List<Resource> resources = this.getResourcesByEmployeeId(employeeId);
        return resources.stream().filter(resource -> resource.getBillingStartDate().compareTo(onDate) <=0 &&
                resource.getBillingEndDate().compareTo(onDate)>=0).findFirst().orElse(null);
    }


    public void deleteResource(Resource resourceReq, String loginEmpId) {
       resourceRepo.delete(resourceReq);

    }

    public void deleteAndUpdateAllocation(Resource resourceReq,String loginEmpId){
        List<Resource> empAllAllocations = resourceRepo.findByEmployeeId(resourceReq.getEmployeeId());
        Resource latestAllocation = this.getLatestAllocation(empAllAllocations.stream().filter(r -> !r.getId().equals(resourceReq.getId())).collect(Collectors.toList()));
        Resource resource = resourceRepo.findById(resourceReq.getId());
        if(resource != null) {
            if (latestAllocation != null && latestAllocation.getProjectId().equalsIgnoreCase(MyTeamUtils.BENCH_PROJECT_ID)) {
                latestAllocation.setBillingEndDate(projectService.getProjectByProjectId(MyTeamUtils.BENCH_PROJECT_ID).getProjectEndDate());
                this.updateExistedResource(latestAllocation);

            }
            this.deleteResource(resourceReq, loginEmpId);
        }else{
            respMap.put("statusCode", 811);
            respMap.put("message", "Record Not Found");
        }
    }

    @Override
    public List<Resource> getAllResources() {
        return resourceRepo.findAll();
    }


    public List<ResourceVO> getAllResourcesVO() {

        return getAllResources().stream().map(resource -> {

            ResourceVO resourceVO = new ResourceVO();
            resourceVO.setId(resource.getId());
            resourceVO.setProjectId(resource.getProjectId());
            resourceVO.setEmployeeId(resource.getEmployeeId());
            resourceVO.setStatus(resource.getStatus());

            Employee employee = employeeService.getEmployeeById(resource.getEmployeeId());
            if (employee != null) {
                resourceVO.setEmployeeName(employee.getEmployeeName());
                resourceVO.setDesignation(employee.getDesignation());
                resourceVO.setEmailId(employee.getEmailId());
                resourceVO.setMobileNo(employee.getMobileNumber());
            }

            Project project = projectService.getProjectByProjectId(resource.getProjectId());
            if (project != null) {
                resourceVO.setProjectName(project.getProjectName());
                resourceVO.setProjectStartDate(project.getProjectStartDate());
                resourceVO.setProjectEndDate(project.getProjectEndDate());
                resourceVO.setProjectStatus(project.getStatus());
                if (project.getAccountId() != null) {
                    Account account = accountService.getAccountById(project.getAccountId());
                    if (account != null) {
                        resourceVO.setAccountName(account.getAccountName());
                    }
                }
            }

            //Account account=accountService.getAccountById(domainService.getDomainById(project.getProjectId()).getAccountId());

            resourceVO.setBillableStatus(resource.getBillableStatus());
            resourceVO.setBillingStartDate(resource.getBillingStartDate());
            resourceVO.setBillingEndDate(resource.getBillingEndDate());
            resourceVO.setResourceRole(resource.getResourceRole());
            if (resource.getBillingEndDate().compareTo(new Date()) > 0) {
                resourceVO.setResourceStatus(ResourceStatus.ACTIVE.getStatus());
            } else {
                resourceVO.setResourceStatus(ResourceStatus.IN_ACTIVE.getStatus());
            }
            return resourceVO;

        }).collect(Collectors.toList());

    }

    public List<Resource> getResourcesSortByBillingStartDate(String employeeId) {
        Query query = prepareQuery(employeeId, MyTeamUtils.BILLING_START_DATE);
        return mongoTemplate.find(query, Resource.class);
    }

    private Query prepareQuery(String employeeId, String dateColumn) {
        Query query = new Query();
        query.addCriteria(Criteria.where(MyTeamUtils.EMPLOYEE_ID).is(employeeId));
        query.limit(MyTeamUtils.ONE);
        query.with(new Sort(Sort.Direction.DESC, dateColumn));
        return query;
    }

    @Override
    public List<ResourceVO> getActiveResources(String empId) {
        List<ResourceVO> resourcesList = new ArrayList<>();
        for (Resource resource : resourceRepo.findByEmployeeId(empId)) {
            if (resource.getBillingEndDate().compareTo(new Date()) > 0) {
                resourcesList.addAll(prepareProjectTeamMembersList(resource.getProjectId()));
            }
        }
        return resourcesList.stream().sorted((o1, o2) -> o1.getEmployeeName().trim().compareTo(o2.getEmployeeName().trim()))
				.collect(Collectors.toList());
    }


    public List<ResourceVO> prepareProjectTeamMembersList(String projectId) {
        List<ResourceVO> finalResourcesList = new ArrayList<>();
        Employee employee = null;

        for (Resource resource : getAllResourcesForProject(projectId)) {

            ResourceVO resourceVO = new ResourceVO();
            resourceVO.setId(resource.getId());
            resourceVO.setProjectId(resource.getProjectId());
            resourceVO.setEmployeeId(resource.getEmployeeId());
            resourceVO.setStatus(resource.getStatus());

            employee = employeeService.getEmployeeById(resource.getEmployeeId());
            resourceVO.setEmployeeName(employee.getEmployeeName());
            resourceVO.setDesignation(employee.getDesignation());
            resourceVO.setEmailId(employee.getEmailId());
            resourceVO.setMobileNo(employee.getMobileNumber());

            resourceVO.setProjectName(projectService.getProjectByProjectId(resource.getProjectId()).getProjectName());
            resourceVO.setBillableStatus(resource.getBillableStatus());
            resourceVO.setBillingStartDate(resource.getBillingStartDate());
            resourceVO.setBillingEndDate(resource.getBillingEndDate());
            resourceVO.setResourceRole(resource.getResourceRole());
            if (resource.getBillingEndDate().compareTo(new Date()) > 0) {
                resourceVO.setResourceStatus(ResourceStatus.ACTIVE.getStatus());
            } else {
                resourceVO.setResourceStatus(ResourceStatus.IN_ACTIVE.getStatus());
            }
            if(projectId.equalsIgnoreCase(MyTeamUtils.BENCH_PROJECT_ID) && employee.getEmpStatus().equalsIgnoreCase(MyTeamUtils.ACTIVE) 
            		&& (resource.getBillingEndDate().compareTo(new Date())>0)) {
            	finalResourcesList.add(resourceVO);
            }
            else if(!projectId.equalsIgnoreCase(MyTeamUtils.BENCH_PROJECT_ID)){
            	finalResourcesList.add(resourceVO);
            }
        }
        return finalResourcesList;
    }

    public List<Resource> getAllResourcesForProject(String projectId) {
        return resourceRepo.findByProjectId(projectId);
    }


    @Override
    public List<Resource> getAllResourcesForAllActiveProjects() {
        List<Resource> resourceList = new ArrayList<>();

        for (Project activeProject : projectService.getOnlyActiveProjects()) {
            resourceList.addAll(getAllResourcesForProject(activeProject.getProjectId()));
        }

        return resourceList;
    }


    @Override
    public List<ResourceVO> getResourcesForProject(String projectId, String statusFlag) {
        List<ResourceVO> resourcesList = new ArrayList<>();

        for (Resource resource : resourceRepo.findByProjectId(projectId)) {
            Date billingEndDate = resource.getBillingEndDate();
            if (billingEndDate != null) {

                ResourceVO resourceVO = new ResourceVO();
                resourceVO.setId(resource.getId());
                resourceVO.setProjectId(resource.getProjectId());
                resourceVO.setProjectName(projectService.getProjectByProjectId(resource.getProjectId()).getProjectName());
                resourceVO.setResourceRole(resource.getResourceRole());
                resourceVO.setBillingStartDate(resource.getBillingStartDate());
                resourceVO.setBillingEndDate(resource.getBillingEndDate());
                resourceVO.setBillableStatus(resource.getBillableStatus());
                resourceVO.setEmployeeId(resource.getEmployeeId());
                resourceVO.setStatus(resource.getStatus());
                resourceVO.setOnBehalfOf(resource.getOnBehalfOf()!=null?employeeService.getEmployeeById(resource.getOnBehalfOf()).getEmployeeName():"");

                Employee employee = employeeService.getEmployeeById(resource.getEmployeeId());
                resourceVO.setEmailId(employee.getEmailId());
                resourceVO.setEmployeeName(employee.getEmployeeName());
                resourceVO.setDesignation(employee.getDesignation());


                // Active
                if (statusFlag.equals(ResourceStatus.ACTIVE.getStatus()) &&
                        (resource.getStatus().equals(MyTeamUtils.STATUS_ENGAGED) ||
                                (resource.getProjectId().equalsIgnoreCase(MyTeamUtils.BENCH_PROJECT_ID)))) {
                	resourceVO.setResourceStatus(ResourceStatus.ACTIVE.getStatus());
                	if(resource.getProjectId().equalsIgnoreCase(MyTeamUtils.BENCH_PROJECT_ID) && 
                			employee.getEmpStatus().equalsIgnoreCase(MyTeamUtils.ACTIVE) && (resource.getBillingEndDate().compareTo(new Date())>0)) {
                		resourcesList.add(resourceVO);
                	}
                	else if(!resource.getProjectId().equalsIgnoreCase(MyTeamUtils.BENCH_PROJECT_ID)) {
                		resourcesList.add(resourceVO);
                	}
                } else if (statusFlag.equals(ResourceStatus.IN_ACTIVE.getStatus()) && resource.getStatus().equals(MyTeamUtils.STATUS_RELEASED)) {
                    resourceVO.setResourceStatus(ResourceStatus.IN_ACTIVE.getStatus());
                    resourcesList.add(resourceVO);
                } else if (statusFlag.equals(MyTeamUtils.STATUS_PROPOSED) && resource.getStatus().equals(MyTeamUtils.STATUS_PROPOSED)){
                    resourceVO.setResourceStatus(MyTeamUtils.STATUS_PROPOSED);
                    resourcesList.add(resourceVO);
                }else if (statusFlag.equals(MyTeamUtils.ALL))
                    resourcesList.add(resourceVO);

            }

        }
        return resourcesList.stream().sorted((o1, o2) -> o1.getEmployeeName().trim().compareTo(o2.getEmployeeName().trim()))
				.collect(Collectors.toList());
    }



    @Override
    public List<MyProjectAllocationVO> getWorkedProjectsForResource(String empId) {

        Project project = null;
        Account account = null;
        Domain domain = null;
        Employee employee = null;
        List<MyProjectAllocationVO> myProjectList = new ArrayList<>();

        List<Resource> resourcesAllocatedList = resourceRepo.findByEmployeeId(empId);

        if (null != resourcesAllocatedList && !resourcesAllocatedList.isEmpty() && MyTeamUtils.INT_ZERO < resourcesAllocatedList.size()) {
            for (Resource resourceAlloc : resourcesAllocatedList) {

                project = projectService.getProjectByProjectId(resourceAlloc.getProjectId());
                account = accountService.getAccountById(project.getAccountId());
                domain = domainService.getDomainById(project.getDomainId());
                employee = employeeService.getEmployeeById(resourceAlloc.getEmployeeId());

                MyProjectAllocationVO myProject = new MyProjectAllocationVO();
                myProject.setProjectId(project.getProjectId());
                myProject.setProjectName(project.getProjectName());
                myProject.setProjectStartDate(project.getProjectStartDate());
                myProject.setProjectEndDate(project.getProjectEndDate());
                myProject.setProjectStatus(project.getStatus());
                myProject.setAccountName(account.getAccountName());

                myProject.setBillableStatus(resourceAlloc.getBillableStatus());
                myProject.setBillingStartDate(resourceAlloc.getBillingStartDate());
                myProject.setBillingEndDate(resourceAlloc.getBillingEndDate());
                myProject.setShift(employee.getShift());
                if (resourceAlloc.getBillingEndDate().compareTo(new Date()) > 0) {
                    myProject.setResourceStatus(ResourceStatus.ACTIVE.getStatus());
                } else {
                    myProject.setResourceStatus(ResourceStatus.IN_ACTIVE.getStatus());
                }

                if (project.getDeliveryLeadIds() != null) {
                    myProject.setDeliveryLeadIds(employeeService.getDeliveryManagerMap(project.getDeliveryLeadIds()));
                }
                myProjectList.add(myProject);
            }
        }
        return myProjectList;
    }


    @Override
    public List<Resource> getResourcesUnderDeliveryLead(String deliveryLeadId) {
        List<String> projectIdsList = new ArrayList<>();
        List<Resource> resourcesList = new ArrayList<>();

        for (Project project : projectService.getProjectsForDeliveryLead(deliveryLeadId))
            projectIdsList.add(project.getProjectId());

        Query query = new Query(Criteria.where("projectId").in(projectIdsList));
        List<Resource> resourcesListPersisted = mongoTemplate.find(query, Resource.class);

        for (Resource resourceAlloc : resourcesListPersisted) {
            if (!resourceAlloc.getEmployeeId().equals(deliveryLeadId))
                resourcesList.add(resourceAlloc);
        }
        return resourcesList;

    }


    @Override
    public List<ResourceVO> getBillingsForEmployee(String empId) {
        List<ResourceVO> finalList = new ArrayList<>();
        List<Resource> resourcesList = resourceRepo.findByEmployeeId(empId);
        if (resourcesList != null && resourcesList.size() > 0) {
            log.info("The resources billing list before sorting::" + resourcesList);
            //return billingsList.stream().sorted(Comparator.comparing(Billing::getCreatedOn).reversed()).collect(Collectors.toList());
            List<Resource> sortedList = resourcesList.stream().sorted(Comparator.comparing(Resource::getBillingStartDate).reversed()).collect(Collectors.toList());

            finalList = convertResourcesToResourcesVO(sortedList);
        }
        return finalList;
    }

    @Override
    public List<Resource> getBillingsForProject(String empId, String projectId) {
        List<Resource> resourcesList = resourceRepo.findByEmployeeIdAndProjectId(empId, projectId);

        if (resourcesList == null || resourcesList.size() == 0) {
            return resourcesList;
        } else {
            //return billingsList.stream().sorted(Comparator.comparing(Billing::getCreatedOn).reversed()).collect(Collectors.toList());
            return resourcesList.stream().sorted(Comparator.comparing(Resource::getBillingStartDate).reversed()).collect(Collectors.toList());
        }

    }


    @Override
    public List<Employee> getUnAssignedEmployees() {

        List<Employee> notAssignedEmployees = new ArrayList<>();
        List<String> resourceIdsList = new ArrayList<>();

        for (Resource resource : this.getAllResources()) {
            Project project = projectService.getProjectByProjectId(resource.getProjectId());
            if (project != null && project.getStatus() != null && !"Completed".equalsIgnoreCase(project.getStatus())) {
                resourceIdsList.add(resource.getEmployeeId());
            }
        }
        for (Employee employee : employeeService.getAllEmployees()) {
            if (!resourceIdsList.contains(employee.getEmployeeId())) {
                notAssignedEmployees.add(employee);
            }
        }

        return notAssignedEmployees;
    }


    public void deleteResourcesUnderProject(String projectId) {
        Query query = new Query(Criteria.where("projectId").is(projectId));
        List<Resource> list = mongoTemplate.find(query, Resource.class);

        resourceRepo.delete(list);
    }


    private List<ResourceVO> convertResourcesToResourcesVO(List<Resource> resourcesList) {

        List<ResourceVO> finalList = new ArrayList<>();
        if (resourcesList != null && resourcesList.size() > 0) {
            finalList = resourcesList.stream().map(resource -> {

                ResourceVO resourceVO = new ResourceVO();
                resourceVO.setId(resource.getId());
                resourceVO.setProjectId(resource.getProjectId());
                resourceVO.setEmployeeId(resource.getEmployeeId());
                resourceVO.setStatus(resource.getStatus());

                Employee employee = employeeService.getEmployeeById(resource.getEmployeeId());
                if (employee != null) {
                    resourceVO.setEmployeeName(employee.getEmployeeName());
                    resourceVO.setDesignation(employee.getDesignation());
                    resourceVO.setEmailId(employee.getEmailId());
                    resourceVO.setMobileNo(employee.getMobileNumber());
                }

                Project project = projectService.getProjectByProjectId(resource.getProjectId());
                if (project != null) {
                    resourceVO.setProjectName(project.getProjectName());
                    resourceVO.setProjectStartDate(project.getProjectStartDate());
                    resourceVO.setProjectEndDate(project.getProjectEndDate());
                    resourceVO.setProjectStatus(project.getStatus());
                    if (project.getAccountId() != null) {
                        Account account = accountService.getAccountById(project.getAccountId());
                        if (account != null) {
                            resourceVO.setAccountName(account.getAccountName());
                        }
                    }
                }

                //Account account=accountService.getAccountById(domainService.getDomainById(project.getProjectId()).getAccountId());

                resourceVO.setBillableStatus(resource.getBillableStatus());
                resourceVO.setBillingStartDate(resource.getBillingStartDate());
                resourceVO.setBillingEndDate(resource.getBillingEndDate());
                resourceVO.setResourceRole(resource.getResourceRole());
                if (resource.getBillingEndDate().compareTo(new Date()) > 0) {
                    resourceVO.setResourceStatus(ResourceStatus.ACTIVE.getStatus());
                } else {
                    resourceVO.setResourceStatus(ResourceStatus.IN_ACTIVE.getStatus());
                }
                return resourceVO;

            }).collect(Collectors.toList());
        }

        return finalList;
    }


    @Override
    public Resource addResourceToBenchProject(Employee employee, String loginEmpId) throws MyTeamException {
        Resource resourcePersisted = null;

        Resource resourceBench = new Resource();
        resourceBench.setAuditFields(loginEmpId, MyTeamUtils.CREATE);
        resourceBench.setProjectId(MyTeamUtils.BENCH_PROJECT_ID);
        resourceBench.setEmployeeId(employee.getEmployeeId());
        resourceBench.setResourceRole(employee.getRole());
        resourceBench.setStatus(MyTeamUtils.STATUS_RELEASED);
        resourceBench.setBillingStartDate(employee.getDateOfJoining() != null ? employee.getDateOfJoining() : new Date());
        resourceBench.setBillableStatus(MyTeamUtils.BENCH_BILLABILITY_STATUS);
        resourceBench.setEmployeeId(employee.getEmployeeId());
        resourceBench.setBillingEndDate(projectService.getProjectByProjectId(MyTeamUtils.BENCH_PROJECT_ID).getProjectEndDate());
//        resourcePersisted = addResource(resourceBench, loginEmpId);
        resourcePersisted = resourceRepo.save(resourceBench);
        respMap.put("message","Resource has been created");
        

        return resourcePersisted;

    }


    @Override
    public List<EmployeeShiftsVO> getResourcesForShift(String shift) {
        List<Resource> resourcesListPers = null;
        List<EmployeeShiftsVO> resourcesList = new ArrayList<>();

        List<Project> projects = projectService.getAllProjects();
        for (Project project : projects) {
            if ("Active".equalsIgnoreCase(project.getStatus())) {
                resourcesListPers = getAllResourcesForProject(project.getProjectId());

                for (Resource resource : resourcesListPers) {

//                    EmployeeShift empShift = empShiftService.getEmployeeShift(resource.getEmployeeId());
//                    if (empShift != null) {
//                        if (empShift.getShift() != null && empShift.getShift().equalsIgnoreCase(shift))
//                            resourcesList.add(resource);
//                        else if (empShift.getShift() == null && Shifts.SHIFT1.getShiftType().equalsIgnoreCase(shift))
//                            resourcesList.add(resource);
//
//                    }

                    if (resource.getBillingEndDate().compareTo(new Date()) >= 0) {
                        Employee employee = employeeService.getEmployeeById(resource.getEmployeeId());

                        EmployeeShiftsVO shiftsVO = new EmployeeShiftsVO();
                        shiftsVO.setEmployeeId(employee.getEmployeeId());
                        shiftsVO.setEmployeeName(employee.getEmployeeName());
                        shiftsVO.setEmailId(employee.getEmailId());
                        shiftsVO.setMobileNo(employee.getMobileNumber());
                        shiftsVO.setProjectName(project.getProjectName());

                        if (employee != null) {
                            if (shift.equalsIgnoreCase(employee.getShift()))
                                resourcesList.add(shiftsVO);
                            else if (employee.getShift() == null && Shifts.SHIFT1.getShiftType().equalsIgnoreCase(shift))
                                resourcesList.add(shiftsVO);

                        }
                    }

                }//for
            }

        }
        return resourcesList;

    }

    @Override
    public Resource getLatestResourceByEmpId(String employeeId) {
        return getLatestAllocation(resourceRepo.findByEmployeeId(employeeId));
       
    }


    @Override
    public List<Resource> getResourcesByBillingStatus(String resourceStatus) {
        return resourceRepo.findByBillableStatus(resourceStatus);
    }

    @Override
    public List<ReserveReportsVO> getResourceReportsByBillingStatus(String resourceStatus) {
        return prepareReserveReports(getResourcesByBillingStatus(resourceStatus));
    }

    @Override
    public List<ReserveReportsVO> prepareReserveReports(List<Resource> resourcesList) {

        List<ReserveReportsVO> reserveReportsList = new ArrayList<>();
        if (resourcesList != null && resourcesList.size() > 0) {
        	resourcesList = resourcesList.stream().filter(r -> isAllocationActiveToday(r)).collect(Collectors.toList());
            Project project = null;
            for (Resource resource : resourcesList) {
                ReserveReportsVO reserveReportsVO = new ReserveReportsVO();
                reserveReportsVO.setEmployeeId(resource.getEmployeeId());
                reserveReportsVO.setEmployeeName(employeeService.getEmployeeById(resource.getEmployeeId()).getEmployeeName());
                if (StringUtils.isNotBlank(resource.getProjectId())) {
                    project = projectService.getProjectByProjectId(resource.getProjectId());
                    if (project != null) {
                        reserveReportsVO.setProjectName(project.getProjectName());
                        reserveReportsVO.setAccountName(accountService.getAccountById(project.getAccountId()).getAccountName());
                    }

                }
                reserveReportsVO.setBillingStartDate(resource.getBillingStartDate());
                reserveReportsVO.setBillingEndDate(resource.getBillingEndDate());

                reserveReportsList.add(reserveReportsVO);
            }
        }

        return reserveReportsList;

    }

    @Override
    public List<Resource> getResourceByProjectId(String projectId){
        return resourceRepo.findByProjectId(projectId);
    }


    public List<ChangedResourceVO> getChangedResourceByDate(String fromDatestr, String toDatestr) {
//        List<ChangedResourceVO> changedResourceVOList = new ArrayList();
//        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//
//        try {
//            final Date fromDate = format.parse(fromDatestr);
//            final Date toDate = format.parse(toDatestr);
//            resourceRepo.findAll().stream().
//                    filter(r -> r.getBillingStartDate().before(toDate)&&r.getBillingEndDate().after(fromDate)).forEach(r -> {
//                ChangedResourceVO crv = new ChangedResourceVO();
//                Project project = projectService.getProjectByProjectId(r.getProjectId());
//                Employee emp = employeeService.getEmployeeById(r.getEmployeeId());
//                Account account = accountService.getAccountById(project.getAccountId());
//
//
//                if(changedResourceVOList.isEmpty()){
//                    crv.setEmplyeeId(r.getEmployeeId());
//                    crv.setEmployeeName(emp.getEmployeeName());
//                    crv.setPrevBillingStatus(r.getBillableStatus());
//                    crv.setPrevBillingStartingDate(r.getBillingStartDate());
//                    crv.setPrevBillingEndDate(r.getBillingEndDate());
//                    crv.setPrevClient(account.getAccountName());
//                    crv.setPrevProject(project.getProjectName());
//                }else {
//
//                    if(!crvList.isEmpty()){
//
//                    }else{
//
//                    }
//                }
//                changedResourceVOList.add(crv);
//            });
//
//        }catch (Exception e){}
//


        return null;
    }


    public boolean validateResourceAllocationStatus(ResourceAllocationStatus resourceStatus) {
        boolean isValidStatus = false;
        switch (resourceStatus) {

            case TRAINEE:

            case BILLABLE:

            case NON_BILLABLE:

            case RESERVED:
                isValidStatus = true;
                break;
        }

        return isValidStatus;
    }


    public List<Resource> getResourcesGreaterThanBillingStartDate(Date billingStartDate) {
        return resourceRepo.findByBillingStartDateGreaterThan(billingStartDate);
    }

    public List<Resource> getResourcesBetweenBillingStartDates(Date fromDate, Date toDate) {
        return resourceRepo.findByBillingStartDateBetween(fromDate, toDate);
    }

    public List<AllocationChangeVO> getAllocationReports(Date fromDate, Date toDate) {

        return getResourcesBetweenBillingStartDates(fromDate, toDate).stream()
                .filter(resource -> resource.getBillingEndDate().compareTo(new Date()) >= 0).map(resource -> {

                    Project project = null;

                    //Setting Current Billing details.
                    AllocationChangeVO allocationVO = new AllocationChangeVO();
                    allocationVO.setEmployeeId(resource.getEmployeeId());
                    allocationVO.setEmployeeName(employeeService.getEmployeeById(resource.getEmployeeId()).getEmployeeName());

                    if (StringUtils.isNotBlank(resource.getProjectId())) {
                        project = projectService.getProjectByProjectId(resource.getProjectId());
                        if (project != null) {
                            allocationVO.setCurrentProjectName(project.getProjectName());
                            allocationVO.setCurrentAccountName(accountService.getAccountById(project.getAccountId()).getAccountName());
                        }
                    }
                    allocationVO.setCurrentBillingStatus(resource.getBillableStatus());
                    allocationVO.setCurrentBillingStartDate(resource.getBillingStartDate());
                    allocationVO.setCurrentBillingEndDate(resource.getBillingEndDate());

                    Resource prevBilling = resourceRepo.findOneByEmployeeIdAndBillingEndDate(resource.getEmployeeId(), MyTeamDateUtils.getDayLessThanDate(resource.getBillingStartDate()));
                    log.info("\n\n\n The prev billing info is::" + prevBilling);

                    if (prevBilling != null) {
                        if (StringUtils.isNotBlank(prevBilling.getProjectId())) {
                            project = projectService.getProjectByProjectId(prevBilling.getProjectId());
                            if (project != null) {
                                allocationVO.setPrevProjectName(project.getProjectName());
                                allocationVO.setPrevAccountName(accountService.getAccountById(project.getAccountId()).getAccountName());
                            }
                        }
                        allocationVO.setPrevBillingStatus(prevBilling.getBillableStatus());
                        allocationVO.setPrevBillingStartDate(prevBilling.getBillingStartDate());
                        allocationVO.setPrevBillingEndDate(prevBilling.getBillingEndDate());
                    }
                    return allocationVO;
                }).collect(Collectors.toList());

    }

    public List<AllocationChangeVO> prepareAllocationResources(List<Resource> resourcesList) {
        List<AllocationChangeVO> allocationList = new ArrayList<>();

        if (resourcesList != null && resourcesList.size() > 0) {
            Project project = null;
            for (Resource resource : resourcesList) {

                AllocationChangeVO allocationVO = new AllocationChangeVO();
                allocationVO.setEmployeeId(resource.getEmployeeId());
                allocationVO.setEmployeeName(employeeService.getEmployeeById(resource.getEmployeeId()).getEmployeeName());

                if (StringUtils.isNotBlank(resource.getProjectId())) {
                    project = projectService.getProjectByProjectId(resource.getProjectId());
                    if (project != null) {
                        allocationVO.setCurrentProjectName(project.getProjectName());
                        allocationVO.setCurrentAccountName(accountService.getAccountById(project.getAccountId()).getAccountName());
                    }

                }
                allocationVO.setCurrentBillingStatus(resource.getBillableStatus());
                allocationVO.setCurrentBillingStartDate(resource.getBillingStartDate());
                allocationVO.setCurrentBillingEndDate(resource.getBillingEndDate());

                allocationList.add(allocationVO);
            }

        }

        return allocationList;
    }

    
    @Override
    public Set<Resource> findByBillableStatus(String billableStatus) {
        return resourceRepo.findByBillableStatus(billableStatus).stream().filter(r -> r.getBillingEndDate().after(new Date())).collect(Collectors.toSet());
    }

	public Resource sendResourceToOpenPool(Resource resource,String loginId) {
        Resource resourcePers = null;
		Resource proposedResource=resourceRepo.findOneByEmployeeIdAndStatus(resource.getEmployeeId(),MyTeamUtils.STATUS_PROPOSED);


		 Resource existingresource = resourceRepo.findById(resource.getId());

		 existingresource.setStatus(MyTeamUtils.STATUS_RELEASED);
		 this.updateExistedResource(existingresource);

		 // isResourceAvailableinBenchbygraterthanEndDate=

              Resource benchResource = new Resource();
              benchResource.setProjectId(MyTeamUtils.BENCH_PROJECT_ID);
              benchResource.setEmployeeId(resource.getEmployeeId());
              benchResource.setResourceRole(resource.getResourceRole());
              benchResource.setBillingStartDate(MyTeamDateUtils.getDayMoreThanDate(resource.getBillingEndDate()));
              benchResource.setBillableStatus(MyTeamUtils.BENCH_BILLABILITY_STATUS);
              benchResource.setStatus(MyTeamUtils.STATUS_RELEASED);
              benchResource.setAuditFields(loginId, MyTeamUtils.CREATE);
        if (proposedResource != null) {
            benchResource.setBillingEndDate(MyTeamDateUtils.getDayLessThanDate(proposedResource.getBillingStartDate()));
            if( proposedResource.getBillingStartDate().compareTo(MyTeamDateUtils.getDayMoreThanDate(existingresource.getBillingEndDate()))!=0) {
                resourcePers = resourceRepo.save(benchResource);
                respMap.put("message", "Resource is moved to Bench Successfully");
            }else {
                respMap.put("message", "Resource Released successfully");
            }

        } else {
            benchResource.setBillingEndDate(projectService.getProjectByProjectId(MyTeamUtils.BENCH_PROJECT_ID).getProjectEndDate());
            resourcePers = resourceRepo.save(benchResource);
            respMap.put("message", "Resource is moved to Bench Successfully");
        }

        return resourcePers;
	}

	@Override
	public Resource getCurrentAllocation(String employeeId) {
			return resourceRepo.findByEmployeeId(employeeId).stream().filter(resource-> isAllocationActiveToday(resource)).findAny().orElse(getLatestResourceByEmpId(employeeId));
	}
	public Resource getCurrentAllocationIfNotReturnNull(String employeeId) {
		return resourceRepo.findByEmployeeId(employeeId).stream().filter(resource-> isAllocationActiveToday(resource)).findAny().orElse(null);
	}

	@Override
	public Resource makeResourceInactive(String employeeId,Date endDate){
        Resource futureAllocation = resourceRepo.findOneByEmployeeIdAndStatus(employeeId, MyTeamUtils.STATUS_PROPOSED);
        Resource currentAllocation = resourceRepo.findOneByEmployeeIdAndStatus(employeeId, MyTeamUtils.STATUS_ENGAGED);
        Resource latestAllocation = this.getLatestResourceByEmpId(employeeId);
        if(Objects.nonNull(futureAllocation)){
            resourceRepo.delete(futureAllocation);
        }
        if(Objects.nonNull(currentAllocation)) {
        	Date projectEndDate = currentAllocation.getBillingEndDate().compareTo(endDate) <= 0 ? currentAllocation.getBillingEndDate() : endDate ;
            currentAllocation.setBillingEndDate(projectEndDate);
            currentAllocation.setStatus(MyTeamUtils.STATUS_RELEASED);
            resourceRepo.save(currentAllocation);
        }
        if(Objects.nonNull(latestAllocation) && latestAllocation.getStatus().equals(MyTeamUtils.STATUS_RELEASED) 
        		&& latestAllocation.getProjectId().equals(MyTeamUtils.BENCH_PROJECT_ID)) {
        	latestAllocation.setBillingEndDate(endDate);
        	resourceRepo.save(latestAllocation);
        }
        return null;
    }

    public Map<String,List<Resource>>  getAllocationOfDateMap(List<String> employeeIdList,Date onDate){
        Query query = new Query(Criteria.where("employeeId").in(employeeIdList).andOperator(Criteria.where("billingStartDate").lte(onDate),Criteria.where("billingEndDate").gte(onDate)));
        List<Resource> resourceList = mongoTemplate.find(query, Resource.class);
        Map<String,List<Resource>> resourceMap = resourceList.stream().collect(Collectors.groupingBy(Resource::getEmployeeId,Collectors.toList()));
        System.out.println(resourceMap);
        return resourceMap;
    }
}