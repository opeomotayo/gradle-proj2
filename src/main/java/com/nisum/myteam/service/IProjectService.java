package com.nisum.myteam.service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Account;
import com.nisum.myteam.model.dao.EmpLoginData;
import com.nisum.myteam.model.dao.Project;
import com.nisum.myteam.model.dao.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface IProjectService {

    Project createProject(Project project, String loginEmpId) throws MyTeamException;

    Project updateProject(Project project, String loginEmpId) throws MyTeamException;

    void deleteProject(String projectId);

    public boolean isProjectExistsByName(String projectName);

    public boolean isProjectExistsById(String projectId);

    public Project getProjectByProjectId(String projectId);

    public long getProjectsCount();

    public List<Project> getAllProjects();

    List<HashMap<Object, Object>> getProjects() throws MyTeamException;

    public List<Project> getOnlyActiveProjects();

    List<Project> getProjectsUnderDomain(String domainId);

    List<Project> getProjectsUnderDeliveryLead(String managerId) throws MyTeamException;

//    public Resource addNewBeanchAllocation(Employee employee, String loginEmpId);

    List<EmpLoginData> employeeLoginsBasedOnDate(long id, String fromDate, String toDate) throws MyTeamException;

    String generatePdfReport(long id, String fromDate, String toDate) throws MyTeamException;

    public Set<String> accountsAssignedToDl(String empId);

    public List<HashMap<Object, Object>> deliveryLeadProjects(String empId) throws MyTeamException;

    public Account getProjectAccount(String accountId);

    public Account updateProjSeqinAccount(Account account) throws MyTeamException;

    public List<HashMap<Object, Object>> getRoleBasedProjects(String empId) throws MyTeamException;

    public List<HashMap<Object, Object>> getProjectsInsteadOfRole() throws MyTeamException;

    public List<Project> getActiveProjects() throws MyTeamException;

    public List<Project> getProjectsForDeliveryLead(String deliveryLeadId);

    List<Resource> getResourcesUnderProject(String empId);

    public List<Project> getProjectsByAccountId(String accId);


}
