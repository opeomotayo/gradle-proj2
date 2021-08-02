package com.nisum.myteam.service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.dao.Resource;
import com.nisum.myteam.model.vo.EmployeeShiftsVO;
import com.nisum.myteam.model.vo.MyProjectAllocationVO;
import com.nisum.myteam.model.vo.ReserveReportsVO;
import com.nisum.myteam.model.vo.ResourceVO;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IResourceService {


    Resource addResource(Resource resourceAllocation, String loginEmpId) throws MyTeamException;

    public void updateExistedResource(Resource resourceAlloc) throws MyTeamException;

    public void insertNewResourceWithNewStatus(Resource resourceAllocReq, String loginEmpId) throws MyTeamException;

    void deleteResource(Resource resource, String loginEmpId);

    List<Resource> getAllResourcesForAllActiveProjects();

    List<Resource> getResourcesSortByBillingStartDate(String employeeId);

    List<ResourceVO> getActiveResources(String empId);


    public List<ResourceVO> getResourcesForProject(String projectId, String statusFlag);

    public List<Resource> getResourcesUnderDeliveryLead(String empId);


    public List<ResourceVO> getBillingsForEmployee(String empId);


    public List<Resource> getBillingsForProject(String empId, String projectId);


    public List<MyProjectAllocationVO> getWorkedProjectsForResource(String empId);


    public List<Employee> getUnAssignedEmployees();

    public List<Resource> getAllResources();

    public List<ResourceVO> getAllResourcesVO();

    public void deleteResourcesUnderProject(String projectId);

    public Resource addResourceToBenchProject(Employee employee, String loginEmpId) throws MyTeamException;


    public List<EmployeeShiftsVO> getResourcesForShift(String shift);

    public Resource getLatestResourceByEmpId(String employeeId);

    public List<Resource> getResourcesByBillingStatus(String resourceStatus);

    public List<ReserveReportsVO> prepareReserveReports(List<Resource> resourcesList);

    public List<ReserveReportsVO> getResourceReportsByBillingStatus(String resourceStatus);

	Set<Resource> findByBillableStatus(String billableStatus);

    public List<Resource> getResourceByProjectId(String projectId);

	Resource getCurrentAllocation(String employeeId);

    public Resource makeResourceInactive(String employeeId,Date endDate);

    // List<Resource> getAllResourcesForProject(String projectId, String status);

    //  List<Resource> getResourcesForEmployee(String empId);

    // List<Resource> getAllResourcesForProject(String projectId);

    // Resource save(Resource resource);

    //  void addResources(Employee employee, String loginEmpId);

    // void inactivateResource(Employee employeeReq, Employee employeeUpdated, String loginEmpId);

}
