package com.nisum.myteam.controller;


import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.exception.handler.ResponseDetails;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.dao.Resource;
import com.nisum.myteam.model.vo.*;
import com.nisum.myteam.repository.EmployeeVisaRepo;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IProjectService;
import com.nisum.myteam.service.impl.ResourceService;
import com.nisum.myteam.utils.MyTeamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@Slf4j
public class ResourceController {

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private IProjectService projectService;

    @Autowired
    private EmployeeVisaRepo employeeVisaRepo;

    @Autowired
    private ResourceService resourceService;

    @RequestMapping(value = "/resources", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createResource(@RequestBody Resource resourceAllocationReq,
                                            @RequestParam(value = "loginEmpId", required = true) String loginEmpId, HttpServletRequest request) throws MyTeamException {
        if (StringUtils.isNotBlank(loginEmpId)) {
            resourceAllocationReq.setAuditFields(loginEmpId, MyTeamUtils.CREATE);

            if (resourceService.validateBillingStartEndDateAgainstProjectStartEndDate(resourceAllocationReq, loginEmpId)) {
                if (resourceService.validateBillingStartDateAgainstDOJ(resourceAllocationReq)) {
                    if (resourceService.isResourceAvailable(resourceAllocationReq)) {
                        if (resourceService.validateAllocationAgainstPrevAllocation(resourceAllocationReq)) {
                            Resource resourcePersisted = resourceService.addResource(resourceAllocationReq, loginEmpId);

                            ResponseDetails createResponseDetails = new ResponseDetails(new Date(), 800, resourceService.respMap.get("message").toString(),
                                    "Resource description", null, request.getContextPath(), "details", resourcePersisted);
                            return new ResponseEntity<ResponseDetails>(createResponseDetails, HttpStatus.OK);
                        }

                    }
                }
            }

            ResponseDetails responseDetails = new ResponseDetails(new Date(), Integer.parseInt(resourceService.respMap.get("statusCode").toString()), resourceService.respMap.get("message").toString(),
                    "Error message desc", null, request.getRequestURI(), "Resource details", resourceAllocationReq);
            return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);

        }
        ResponseDetails responseDetails = new ResponseDetails(new Date(), 820, "Please provide the valid Employee Id",
                "Employee Id is not valid", null, request.getRequestURI(), "Resource details", resourceAllocationReq);
        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);

    }

    @RequestMapping(value = "/resources", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateResource(@RequestBody Resource resourceAllocationReq,
                                            @RequestParam(value = "loginEmpId") String loginEmpId, HttpServletRequest request) throws MyTeamException {

        if (StringUtils.isNotBlank(loginEmpId)) {
            resourceAllocationReq.setAuditFields(loginEmpId, MyTeamUtils.UPDATE);

            if (resourceService.isResourceExistsForProject(resourceAllocationReq.getEmployeeId(), resourceAllocationReq.getProjectId())) {


                resourceService.updateResourceDetails(resourceAllocationReq, loginEmpId);
//                resourceService.insertNewResourceWithNewStatus(resourceAllocationReq, loginEmpId);
            }

            ResponseDetails createResponseDetails = new ResponseDetails(new Date(), Integer.parseInt(resourceService.respMap.get("statusCode").toString()),
                    resourceService.respMap.get("message").toString(), "Resource description", null, request.getContextPath(),
                    "Resource details", resourceAllocationReq);
            return new ResponseEntity<ResponseDetails>(createResponseDetails, HttpStatus.OK);
        }
        ResponseDetails responseDetails = new ResponseDetails(new Date(), 820, "Please provide the valid Employee Id",
                "Employee Id is not valid", null, request.getRequestURI(), "Resource details", resourceAllocationReq);
        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);
    }

    @RequestMapping(value = "/resources", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteResource(@RequestBody Resource resourceReq,
                                            @RequestParam(value = "loginEmpId", required = true) String loginEmpId, HttpServletRequest request) throws MyTeamException {
        if (StringUtils.isNotBlank(loginEmpId)) {
            resourceService.deleteAndUpdateAllocation(resourceReq, loginEmpId);

            ResponseDetails createResponseDetails = new ResponseDetails(new Date(), 601, "Resource has been deleted",
                    "Resource description", null, request.getContextPath(), "Resource details", resourceReq);
            return new ResponseEntity<ResponseDetails>(createResponseDetails, HttpStatus.OK);
        }

        ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Please provide the valid Employee Id",
                "Employee Id is not valid", null, request.getRequestURI(), "Resource details", resourceReq);
        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);
    }


    @RequestMapping(value = "/resources/project/{projectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getResourcesForProject(@PathVariable(value = "projectId", required = true) String projectId,
                                                    @RequestParam(value = "status", required = false, defaultValue = MyTeamUtils.ACTIVE) String status,
                                                    HttpServletRequest request)
            throws MyTeamException {

        if (StringUtils.isNotBlank(projectId)) {
            List<ResourceVO> resourcesList = resourceService.getResourcesForProject(projectId, status);
            ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Resources have been retrieved successfully",
                    "List of Resources for a project", resourcesList, request.getRequestURI(), "Resource details", null);
            return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);

        }
        ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Please provide ProjectId",
                "List of Resources for a project", null, request.getRequestURI(), "Resource details", null);
        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);
    }

    @RequestMapping(value = "/resources/getMyProjectAllocations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMyProjectAllocations(
            @RequestParam("employeeId") String employeeId, HttpServletRequest request) throws MyTeamException {

        if (employeeId != null && !"".equalsIgnoreCase(employeeId)) {
            ResponseDetails getRespDetails = new ResponseDetails(new Date(), 604, "Retrieved the project allocations successfully",
                    "Projects allocations for an employee", resourceService.getWorkedProjectsForResource(employeeId), request.getRequestURI(), "Project details", null);
            return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
        }

        ResponseDetails getRespDetails = new ResponseDetails(new Date(), 606, "Please Provide valid employee  id",
                "Project allocations for an employee", null, request.getRequestURI(), "Project details", null);
        return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

    }



    //ok
    ///resourceAllocation/projects has to be changed to /resourceAllocation/activeProjects
    @RequestMapping(value = "/resources/projects", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getResourcesAllocatedForAllProjects(HttpServletRequest request) throws MyTeamException {

        List<Resource> resourcesList = resourceService.getAllResourcesForAllActiveProjects();
        ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Resources have been retrieved successfully",
                "List of Resources for the  projects", resourcesList, request.getRequestURI(), "Resource details", null);
        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);

    }


    //ok //Getting Current active resource record
    @RequestMapping(value = "/resources/employeeId/{employeeId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getResourcesSortByProjectStartDate(@PathVariable(value = "employeeId", required = true) String employeeId, HttpServletRequest request)
            throws MyTeamException {

        if (StringUtils.isNotBlank(employeeId)) {
            List<Resource> resourcesList = resourceService.getResourcesSortByBillingStartDate(employeeId);
            ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Resources have been retrieved successfully",
                    "List of Resources for an employee", resourcesList, request.getRequestURI(), "Resource List details", null);
            return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);
        }

        ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Please provide the valid Employee Id",
                "Employee Id is not valid", null, request.getRequestURI(), "Resource details", null);
        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);

    }


    @RequestMapping(value = "/resources/active", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveResources(@RequestParam(value = "employeeId", required = false) String employeeId, HttpServletRequest request)
            throws MyTeamException {
        if (StringUtils.isNotBlank(employeeId)) {
            List<ResourceVO> employeesRoles = resourceService.getActiveResources(employeeId);

            ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Resources have been retrieved successfully",
                    "List of Resources who are active in the projects", employeesRoles, request.getRequestURI(), "Resource List details", null);
            return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);

        }
        ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Please provide the valid Employee Id",
                "Employee Id is not valid", null, request.getRequestURI(), "Resource details", null);
        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);
    }



    @RequestMapping(value = "/resources/deliverylead/{deliveryLeadId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTeamDetails(@PathVariable(value = "deliveryLeadId", required = true) String deliveryLeadId, HttpServletRequest request)
            throws MyTeamException {

        if (StringUtils.isNotBlank(deliveryLeadId)) {
            List<Resource> resourcesList = resourceService.getResourcesUnderDeliveryLead(deliveryLeadId);

            ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Resources have been retrieved successfully",
                    "List of Resources for a Delivery Lead", resourcesList, request.getRequestURI(), "Resource details", null);
            return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);
        }

        ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Please provide Valid Delivery Lead Id",
                "List of Resources for DeliveryLead", null, request.getRequestURI(), "Resource details", null);
        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);

    }

    @RequestMapping(value = "/resources/unAssignedEmployees", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUnAssignedEmployees(HttpServletRequest request) throws MyTeamException {
        List<Employee> employeesList = resourceService.getUnAssignedEmployees();

        ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Resources have been retrieved successfully",
                "List of Resources who are not assigned to project", employeesList, request.getRequestURI(), "Resource details", null);
        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);
    }


    @RequestMapping(value = "/resources/billing", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResourceVO>> getAllBillingsForEmployee(@RequestParam("employeeId") String employeeId)
            throws MyTeamException {
        List<ResourceVO> resourceAllocList = resourceService.getBillingsForEmployee(employeeId);
        return new ResponseEntity<>(resourceAllocList, HttpStatus.OK);
    }

    @RequestMapping(value = "/resources/billing/project/{projectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Resource>> getBillingsForProject(@PathVariable("projectId") String projectId,
                                                                @RequestParam("employeeId") String employeeId) throws MyTeamException {
        List<Resource> resourceAllocList = resourceService.getBillingsForProject(employeeId, projectId);
        return new ResponseEntity<>(resourceAllocList, HttpStatus.OK);
    }

 
    @RequestMapping(value = "/resources/shifts/{shift}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getResourcesForShift(@PathVariable(value = "shift", required = true) String shift, HttpServletRequest request)
            throws MyTeamException {

        if (StringUtils.isNotBlank(shift)) {
            List<EmployeeShiftsVO> resourcesList = resourceService.getResourcesForShift(shift);
            ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Resources have been retrieved successfully",
                    "List of Resources for the provided shift", resourcesList, request.getRequestURI(), "Resource List for shift", null);
            return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);
        }
        ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Please provide the valid Shift value",
                "List of Resources for the provided shift", null, request.getRequestURI(), "Resource details", null);
        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);

    }

    @RequestMapping(value="getResourceAllocation/{fromDate}/{toDate}",method = RequestMethod.GET ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUpdatedResourceAllocationsByDate(@PathVariable("") String fromDate,@PathVariable String toDate){
        List<ChangedResourceVO> changedResourceVOList = resourceService.getChangedResourceByDate(fromDate,toDate);
        return null;
    }


    @RequestMapping(value = "/resources/reports", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReserveReports(@RequestParam(value = "resourceStatus",defaultValue = "Reserved") String resourceStatus, HttpServletRequest request)
            throws MyTeamException {

        List<ReserveReportsVO> reservedResources = resourceService.getResourceReportsByBillingStatus(resourceStatus);

        ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905, "Retrieved Resources List successfully",
                "Resource Allocation List Details", reservedResources, request.getRequestURI(), "Resource Allocation List Details", null);
        return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

    }

    @RequestMapping(value = "/resources/allocationReports", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllocationChangeReports(@RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
                                                        @RequestParam("toDate")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)Date toDate, HttpServletRequest request)
            throws MyTeamException {

        //List<Resource> allocationResources = resourceService.getResourcesGreaterThanBillingStartDate(fromDate);
        List<AllocationChangeVO> allocationResources = resourceService.getAllocationReports(fromDate,toDate);
        log.info("The resources::"+allocationResources);

        ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905, "Retrieved Resources List successfully",
                "Resource Allocation List Details", allocationResources, request.getRequestURI(), "Resource Allocation List Details", null);
        return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

    }

    @RequestMapping(value = "/resources/moveToOpenPool", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> moveResourceToOpenPool(@RequestBody Resource resource, @RequestParam(value = "loginEmpId") String loginEmpId, HttpServletRequest request) throws MyTeamException {
        ResponseDetails responseDetails;
        if (StringUtils.isNotBlank(loginEmpId)) {
            Resource result= resourceService.sendResourceToOpenPool(resource,loginEmpId);

            responseDetails = new ResponseDetails(new Date(), Integer.parseInt(resourceService.respMap.get("statusCode").toString()),
                    resourceService.respMap.get("message").toString(), "Resource description", null, request.getContextPath(),
                    "Resource details", result);

        }
        else {
            responseDetails = new ResponseDetails(new Date(), 820, "Please provide the valid Employee Id",
                    "Employee Id is not valid", null, request.getRequestURI(), "Resource details", resource);
        }
        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);
    }


}
