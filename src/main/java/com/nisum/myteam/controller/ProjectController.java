package com.nisum.myteam.controller;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.exception.handler.ResponseDetails;
import com.nisum.myteam.model.dao.Account;
import com.nisum.myteam.model.dao.Project;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IProjectService;
import com.nisum.myteam.statuscodes.ProjectStatus;
import com.nisum.myteam.utils.constants.ApplicationRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@RestController
@Slf4j
public class ProjectController {

    @Autowired
    private IProjectService projectService;

    @Autowired
    private IEmployeeService employeeService;


    @RequestMapping(value = "/projects", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProject(@Valid @RequestBody Project projectReq,
                                           @RequestParam(value = "loginEmpId") String loginEmpId, HttpServletRequest request) throws MyTeamException {

        if (!projectService.isProjectExistsByName(projectReq.getProjectName())) {

            String accountName = "";
            Account account = projectService.getProjectAccount(projectReq.getAccountId());
            if (account != null) {
                accountName = account.getAccountName();
                int sequenceNumber = account.getAccountProjectSequence();
                account.setAccountProjectSequence(sequenceNumber + 1);
                projectService.updateProjSeqinAccount(account);


                String projectId = accountName + String.format("%04d", sequenceNumber + 1);
                projectReq.setProjectId(projectId);

                Project projectPersisted = projectService.createProject(projectReq, loginEmpId);

                if (projectPersisted != null) {
                    ResponseDetails getRespDetails = new ResponseDetails(new Date(), ProjectStatus.CREATE.getCode(),
                            ProjectStatus.CREATE.getMessage(), "Project description", null,
                            request.getRequestURI(), "details", projectPersisted);

                    return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
                }

            }

        }
        ResponseDetails getRespDetails = new ResponseDetails(new Date(), ProjectStatus.ALREADY_EXISTED.getCode(),
                ProjectStatus.ALREADY_EXISTED.getMessage(), "Choose the different project name", null,
                request.getRequestURI(), "details", projectReq);

        return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);


    }

    // @RequestMapping(value = "/updateProject"
    @RequestMapping(value = "/projects/{projectId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProject(@Valid @RequestBody Project project, @PathVariable(value = "projectId", required = true) String projectId,
                                           @RequestParam(value = "loginEmpId") String loginEmpId, HttpServletRequest request) throws MyTeamException {

        if (projectService.isProjectExistsById(projectId)) {
        	Project existedProject = projectService.getProjectByProjectId(projectId);
        	if(!existedProject.getProjectName().equalsIgnoreCase(project.getProjectName())) {
        		if (!projectService.isProjectExistsByName(project.getProjectName())) {
        			Project updatedProject = projectService.updateProject(project, loginEmpId);
        			ResponseDetails getRespDetails = new ResponseDetails(new Date(), ProjectStatus.UPDATE.getCode(),
        					ProjectStatus.UPDATE.getMessage(), "Project Updation Description", null,
        					request.getRequestURI(), "Project Updation details", updatedProject);

        			return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
        		}else {
        			 ResponseDetails getRespDetails = new ResponseDetails(new Date(), ProjectStatus.PROJECT_NAME_IS_NOT_EXISTS.getCode(),
        	                    ProjectStatus.PROJECT_NAME_IS_NOT_EXISTS.getMessage(), "Please provide the valid project name", null,
        	                    request.getRequestURI(), "details", project);

        	            return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
        		}
        	}else {
        		Project updatedProject = projectService.updateProject(project, loginEmpId);
    			ResponseDetails getRespDetails = new ResponseDetails(new Date(), ProjectStatus.UPDATE.getCode(),
    					ProjectStatus.UPDATE.getMessage(), "Project Updation Description", null,
    					request.getRequestURI(), "Project Updation details", updatedProject);

            return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
        		
        	}
        }
        ResponseDetails getRespDetails = new ResponseDetails(new Date(), ProjectStatus.PROJECTID_IS_NOT_EXISTS.getCode(),
                ProjectStatus.PROJECTID_IS_NOT_EXISTS.getMessage(), "Please provide the valid project Id", null,
                request.getRequestURI(), "details", project);
        return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

    }

    // @RequestMapping(value = "/deleteProject"
    @RequestMapping(value = "/projects/{projectId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteProject(@PathVariable("projectId") String projectId, HttpServletRequest request) throws MyTeamException {

        if (projectService.isProjectExistsById(projectId)) {
            projectService.deleteProject(projectId);
            ResponseDetails deleteRespDetails = new ResponseDetails(new Date(), ProjectStatus.DELETE.getCode(),
                    ProjectStatus.DELETE.getMessage(), "Project Deletion description", null, request.getRequestURI(), "Project Deletion details",
                    projectId);
            return new ResponseEntity<ResponseDetails>(deleteRespDetails, HttpStatus.OK);

        }

        ResponseDetails deleteRespDetails = new ResponseDetails(new Date(), ProjectStatus.PROJECTID_IS_NOT_EXISTS.getCode(),
                ProjectStatus.PROJECTID_IS_NOT_EXISTS.getMessage(), "Please provide valid project id", null, request.getRequestURI(), "Project Deletion details",
                projectId);
        return new ResponseEntity<ResponseDetails>(deleteRespDetails, HttpStatus.OK);
    }


    // @RequestMapping(value = "/getProjects" //get projects only for DL
    @RequestMapping(value = "/projects/employeeId/{employeeId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProjectsOnRoleBasis(
            @PathVariable(value = "employeeId") String employeeId, HttpServletRequest request)
            throws MyTeamException {
        List<HashMap<Object, Object>> projects = null;

        if (employeeId != null && !"".equalsIgnoreCase(employeeId)) {

            boolean isEmployeeHaveAccess = employeeService.verifyEmployeeRole(employeeId, ApplicationRole.DELIVERY_LEAD.getRoleId());
            if (isEmployeeHaveAccess) {
                //projects = projectService.deliveryLeadProjects(employeeId);

                projects = projectService.getRoleBasedProjects(employeeId);

                ResponseDetails getRespDetails = new ResponseDetails(new Date(), ProjectStatus.GET_PROJECTS.getCode(), ProjectStatus.GET_PROJECTS.getMessage(),
                        "Projects list", projects, request.getRequestURI(), "Project details", null);

                return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
            } else {
                projects = projectService.getProjectsInsteadOfRole();
                ResponseDetails getRespDetails = new ResponseDetails(new Date(), ProjectStatus.GET_PROJECTS.getCode(), ProjectStatus.GET_PROJECTS.getMessage(),
                        "Projects list", projects, request.getRequestURI(), "Project details", null);

                return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
            }


//            ResponseDetails getRespDetails = new ResponseDetails(new Date(), 605, "You are not authorized to view Projects.",
//                    "Only Delivery leads can get the project details", projects, request.getRequestURI(), "Project details", null);
//
//            return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

        }

        ResponseDetails getRespDetails = new ResponseDetails(new Date(), ProjectStatus.EMPLOYEE_NOT_EXISTS.getCode(), ProjectStatus.EMPLOYEE_NOT_EXISTS.getMessage(),
                "Projects list", null, request.getRequestURI(), "Project details", null);

        return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
    }

    @RequestMapping(value = "/projects/deliveryLeadId/{deliveryLeadId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProjectsUnderDeliveryLead(
            @PathVariable("deliveryLeadId") String deliveryLeadId, HttpServletRequest request)
            throws MyTeamException {

        if (deliveryLeadId != null && !"".equalsIgnoreCase(deliveryLeadId)) {

            ResponseDetails getRespDetails = new ResponseDetails(new Date(), ProjectStatus.GET_PROJECTS.getCode(), ProjectStatus.GET_PROJECTS.getMessage(),
                    "Projects list under delivery lead", projectService.getProjectsUnderDeliveryLead(deliveryLeadId), request.getRequestURI(), "Project details", null);
            return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

        }
        ResponseDetails getRespDetails = new ResponseDetails(new Date(), ProjectStatus.DELIVERYLEAD_NOT_EXISTS.getCode(), ProjectStatus.DELIVERYLEAD_NOT_EXISTS.getMessage(),
                "Projects list under delivery lead", null, request.getRequestURI(), "Project details", null);
        return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

    }


}

