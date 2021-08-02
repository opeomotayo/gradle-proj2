package com.nisum.myteam.controller;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.myteam.controller.ResourceController;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.dao.Resource;
import com.nisum.myteam.model.vo.AllocationChangeVO;
import com.nisum.myteam.model.vo.EmployeeShiftsVO;
import com.nisum.myteam.model.vo.MyProjectAllocationVO;
import com.nisum.myteam.model.vo.ReserveReportsVO;
import com.nisum.myteam.model.vo.ResourceVO;
import com.nisum.myteam.repository.EmployeeVisaRepo;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IProjectService;
import com.nisum.myteam.service.impl.ResourceService;
import com.nisum.myteam.utils.MyTeamUtils;

public class ResourceControllerTest {

	@Mock
	IEmployeeService employeeService;
	
	@Mock
	IProjectService projectService;
	
//	@Mock
//	EmployeeVisaRepo employeeVisaRepo;
	
	@Mock
	ResourceService resourceService;
	
	 @InjectMocks
	 ResourceController resourceController;
	
	private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();
    }

    @Test
    public void testCreateResource() throws Exception {
    	Resource resourceAllocationReq = new Resource();
    	String loginEmpId = "";
    	ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(resourceAllocationReq);
    	when(resourceService.addResource(resourceAllocationReq, loginEmpId)).thenReturn(null);
	 	mockMvc.perform(post("/resources?loginEmpId="  + "").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
		.andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test
    public void testCreateResourceExists() throws Exception {
    	Resource resourceAllocationReq = new Resource();
    	String loginEmpId = "";
    	ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(resourceAllocationReq);
    
    	when(resourceService.validateBillingStartEndDateAgainstProjectStartEndDate(resourceAllocationReq, loginEmpId)).thenReturn(true);
  	when(resourceService.validateBillingStartDateAgainstDOJ(resourceAllocationReq)).thenReturn(true);
 	when(resourceService.isResourceAvailable(resourceAllocationReq)).thenReturn(true);
 	when(resourceService.validateAllocationAgainstPrevAllocation(resourceAllocationReq)).thenReturn(true);
    	
    	when(resourceService.addResource(resourceAllocationReq, loginEmpId)).thenReturn(null);
	 	mockMvc.perform(post("/resources?loginEmpId="  + "").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
		.andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    
    @Test
    public void testCreateResourceFalse() throws Exception {
    	Resource resourceAllocationReq = new Resource();
    	String loginEmpId = "";
    	ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(resourceAllocationReq);		
    	when(resourceService.validateBillingStartEndDateAgainstProjectStartEndDate(resourceAllocationReq, loginEmpId)).thenReturn(false);
		
    	when(resourceService.addResource(resourceAllocationReq, loginEmpId)).thenReturn(null);
	 	mockMvc.perform(post("/resources?loginEmpId="  + "").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
		.andExpect(MockMvcResultMatchers.status().isOk());
    }
   
    @Test
    public void testUpdateResource() throws Exception {
    	Resource resourceAllocationReq = new Resource();
    	String loginEmpId = "";
    	ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(resourceAllocationReq);
		when(resourceService.isResourceExistsForProject(anyString(), anyString())).thenReturn(false);
    	//when(resourceService.updateResourceDetails(null, null)).thenReturn(null);	
    	
	 	mockMvc.perform(put("/resources?loginEmpId="  + "").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
		.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteResource() throws Exception {
    	Resource resourceAllocationReq = new Resource();
    	String loginEmpId = "";
    	ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(resourceAllocationReq);
  //  	when(resourceService.deleteAndUpdateAllocation(resourceAllocationReq, loginEmpId)).thenReturn(null);
	 	mockMvc.perform(post("/resources?loginEmpId="  + "").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
		.andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test
    public void testgetResourcesForProjectwithNull() throws Exception {
    	List<ResourceVO> employeePersisted = null;
		when(resourceService.getResourcesForProject(null, null)).thenReturn(employeePersisted);
		mockMvc.perform(get("/resources/project/{projectId}"," "))
				.andExpect(MockMvcResultMatchers.status().isOk());
	
    }
    
    @Test
    public void testgetResourcesForProject() throws Exception {
    	List<ResourceVO> resourcesList = null;
    	ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(resourcesList);
		when(resourceService.getResourcesForProject("", "")).thenReturn(resourcesList);
		mockMvc.perform(get("/resources/project/{projectId}"," ").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
				.andExpect(MockMvcResultMatchers.status().isOk());
	
    }
    

    @Test
    public void testgetMyProjectAllocations() throws Exception {
    	List<MyProjectAllocationVO> employeePersisted = null;
		when(resourceService.getWorkedProjectsForResource(null)).thenReturn(employeePersisted);
		mockMvc.perform(put("/resources/getMyProjectAllocations"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	
    }
    
    @Test
    public void testgetResourcesAllocatedForAllProjects() throws Exception {
    	List<Resource> resourcesList = null ;
		when(resourceService.getAllResourcesForAllActiveProjects()).thenReturn(resourcesList);
		mockMvc.perform(get("/resources/projects"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	
    }
    
    @Test
    public void testgetResourcesSortByBillingStartDate() throws Exception {
    	List<Resource> resourcesList = null ;
    	ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(resourcesList);
		when(resourceService.getResourcesSortByBillingStartDate(anyString())).thenReturn(resourcesList);
		mockMvc.perform(get("/resources/employeeId/{employeeId}","").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
				.andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test
    public void testgetActiveResources() throws Exception {
    	List<ResourceVO> resourcesList = null ;
		when(resourceService.getActiveResources(null)).thenReturn(resourcesList);
		mockMvc.perform(get("/resources/active"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	
    }
    
    @Test
    public void testgetTeamDetails() throws Exception {
    	List<Resource> resourcesList = null ;
		when(resourceService.getResourcesUnderDeliveryLead(null)).thenReturn(resourcesList);
		mockMvc.perform(get("/resources/deliverylead/{deliveryLeadId}"," "))
				.andExpect(MockMvcResultMatchers.status().isOk());
	
    }
    
    @Test
    public void testgetUnAssignedEmployees() throws Exception {
    	List<Employee> resourcesList = null ;
		when(resourceService.getUnAssignedEmployees()).thenReturn(resourcesList);
		mockMvc.perform(get("/resources/unAssignedEmployees"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	
    }
    
    @Test
    public void testgetAllBillingsForEmployee() throws Exception {
    	List<ResourceVO> resourcesList = null ;
		when(resourceService.getBillingsForEmployee(anyString())).thenReturn(resourcesList);
		mockMvc.perform(get("/resources/billing?employeeId" + " "))
				.andExpect(MockMvcResultMatchers.status().isOk());
	
    }
    
    @Test
    public void testgetBillingsForProject() throws Exception {
    	List<Resource> resourcesList = null ;
		when(resourceService.getBillingsForProject(anyString(), anyString())).thenReturn(resourcesList);
		mockMvc.perform(get("/resources/billing/project/{projectId}",anyString()))
				.andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test
    public void testgetResourcesForShift() throws Exception {
    	//List<EmployeeShiftsVO> resourcesList = null ;
    	List<EmployeeShiftsVO> resourcesList = new ArrayList<>();
		when(resourceService.getResourcesForShift(anyString())).thenReturn(resourcesList);
		mockMvc.perform(get("/resources/shifts/{shift}" + anyString()))
				.andExpect(MockMvcResultMatchers.status().isOk());	
    }
    
    @Test
    public void testgetResourceReportsByBillingStatus() throws Exception {
    	List<ReserveReportsVO> resourcesList = null ;
		when(resourceService.getResourceReportsByBillingStatus(anyString())).thenReturn(resourcesList);
		mockMvc.perform(get("/resources/reports"))
				.andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test
    public void testgetAllocationReports() throws Exception {
    	List<AllocationChangeVO> resourcesList = null ;
		when(resourceService.getAllocationReports(null, null)).thenReturn(resourcesList);
		mockMvc.perform(get("/resources/allocationReports"))
				.andExpect(MockMvcResultMatchers.status().isOk());
    }
}
