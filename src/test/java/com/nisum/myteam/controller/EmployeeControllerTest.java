package com.nisum.myteam.controller;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.myteam.controller.EmployeeController;
import com.nisum.myteam.model.dao.EmpLoginData;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.service.IEmployeeRoleService;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.ISubStatusService;
import com.nisum.myteam.service.impl.EmployeeService;

public class EmployeeControllerTest {
	
	@Mock
	private EmployeeService empService;
	
	@Mock
	private IEmployeeRoleService employeeRoleService;
	
	@Mock
	private ISubStatusService subStatusService;
	
	@InjectMocks
	EmployeeController employeeController;
	
	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
	}
	
	@Test
	public void testCreateEmployee() throws Exception {
		Employee employeePersisted = new Employee();
		Employee employeeReq = new Employee();
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(employeeReq);
		when(empService.createEmployee(employeeReq, "16253")).thenReturn(employeePersisted);
		mockMvc.perform(post("/employees/{empId}","16253").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
				.andExpect(MockMvcResultMatchers.status().isOk());
		
	}
	
	
	@Test
	public void testCreateEmployeeExistsById() throws Exception {
		Employee employeePersisted = new Employee();
		Employee employeeReq = new Employee();
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(employeeReq);
		when(empService.isEmployeeExistsById("16253")).thenReturn(true);
		when(empService.createEmployee(employeeReq, "16253")).thenReturn(employeePersisted);
		mockMvc.perform(post("/employees/{empId}","16253").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
				.andExpect(MockMvcResultMatchers.status().isOk());
		
	}
	
	@Test
	public void testUpdateEmployee() throws Exception {
		Employee employeePersisted = new Employee();
		Employee employeeReq = new Employee();
		ObjectMapper mapper = new ObjectMapper();
		when(empService.isEmployeeExistsById("16253")).thenReturn(false);
		String jsonString = mapper.writeValueAsString(employeeReq);
		when(empService.updateEmployee(employeeReq, "16253")).thenReturn(employeePersisted);
		mockMvc.perform(put("/employees/{empId}","16253").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
				.andExpect(MockMvcResultMatchers.status().isOk());
		
	}
	
	@Test
	public void testUpdateEmployeeExistsById() throws Exception {
		Employee employeePersisted = new Employee();
		Employee employeeReq = new Employee();
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(employeeReq);
		when(empService.isEmployeeExistsById("16253")).thenReturn(true);
		when(empService.updateEmployee(employeeReq, "16253")).thenReturn(employeePersisted);
		mockMvc.perform(put("/employees/{empId}","16253").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
				.andExpect(MockMvcResultMatchers.status().isOk());
		
	}
	
	@Test
	public void testDeleteEmployee() throws Exception {
		Employee employeePersisted = new Employee();
		Employee employeeReq = new Employee();
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(employeeReq);
		when(empService.deleteEmployee("16253")).thenReturn(employeePersisted);
		mockMvc.perform(delete("/employees/{empId}","16253").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
				.andExpect(MockMvcResultMatchers.status().isOk());
	
	}

	@Test
	public void testDeleteEmployeeExistsById() throws Exception {
		Employee employeePersisted = new Employee();
		Employee employeeReq = new Employee();
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(employeeReq);
		when(empService.isEmployeeExistsById("16253")).thenReturn(true);
		when(empService.deleteEmployee("16253")).thenReturn(employeePersisted);
		mockMvc.perform(delete("/employees/{empId}","16253").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
				.andExpect(MockMvcResultMatchers.status().isOk());
		  
	}
	
	@Test
	public void testUpdateProfile() throws Exception {
		Employee employeePersisted = new Employee();
		Employee employeeReq = new Employee();
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(employeeReq);
		when(empService.updateProfile(employeeReq)).thenReturn(employeePersisted);
		mockMvc.perform(post("/employees/updateProfile").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
				.andExpect(MockMvcResultMatchers.status().isOk());
	
	}
	
	@Test
	public void testGetEmployeeById() throws Exception {
		Employee employeePersisted = new Employee();
		Employee employeeReq = new Employee();
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(employeeReq);
		when(empService.getEmployeeById("")).thenReturn(employeePersisted);
		mockMvc.perform(get("/employees/employeeId/{empId}", "16253").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
				.andExpect(MockMvcResultMatchers.status().isOk());
	
	}
	
	@Test
	public void testGetEmployee() throws Exception { 
	Employee employeePersisted = new Employee();
//		Employee employeeReq = new Employee();
		Employee employee = null;
		when(empService.getEmployeeByEmaillId("s@nisum.com")).thenReturn(employeePersisted);
		mockMvc.perform(get("/employees/emailId"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	
	}
	
	@Test
	public void testGetManagers() throws Exception {
		
	 	mockMvc.perform(get("/employees/managers/").param("domainId", "ACC001"))
        .andExpect(MockMvcResultMatchers.status().isOk());
        verify(empService).getManagers(); 
	
	}
	
	@Test
	public void testGetActiveEmployees() throws Exception {
		
	 	mockMvc.perform(get("/employees/active").param("domainId", "ACC001"))
        .andExpect(MockMvcResultMatchers.status().isOk());
        verify(empService).getActiveEmployees(); 
	
	}
	
	@Test
	public void testGetAccounts() throws Exception {
		mockMvc.perform(get("/employees/accounts/").param("domainId", "ACC001"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(empService).getAccounts();

	}
	
	@Test
	public void testGetEmployeeByStatus() throws Exception {
		List<Employee> employeeList = null;
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(employeeList);
		when(empService.getEmployeesByStatus(anyString())).thenReturn(employeeList);
	 	mockMvc.perform(get("/employees/").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
		.andExpect(MockMvcResultMatchers.status().isOk());
       // verify(empService).getEmployeesByStatus(""); 
	
	}
	
	@Test
	public void testGetEmployeeRoleDataForSearchCriteria() throws Exception {
		mockMvc.perform(get("/employee/searchCriteria").param("domainId", "ACC001"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(empService).getEmployeeRoleDataForSearchCriteria("", "");

	}
	
	@Test
	public void testgetEmployeeDetailsForAutocomplete() throws Exception {
		mockMvc.perform(get("/employee/autocomplete").param("domainId", "ACC001"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(empService).getEmployeeDetailsForAutocomplete();

	}
	
	@Test
	public void testgetDeliveryLeads() throws Exception {
//		mockMvc.perform(get("/employees/deliveryLeads/{domainId}","2568").param("domainId", "ACC001"))
//				.andExpect(MockMvcResultMatchers.status().isOk());
//		verify(empService).getDeliveryLeads(null);
		
		when(empService.getDeliveryLeads(anyString())).thenReturn(null);
	 	mockMvc.perform(get("/employees/deliveryLeads/{domainId}","2568"))
		.andExpect(MockMvcResultMatchers.status().isOk());

	}
	
	@Test
	public void testendSubStatus() throws Exception {
		mockMvc.perform(put("/subStatus").param("domainId", "ACC001"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(subStatusService).endSubStatus(null, null);

	}

	@Test
	public void testemployeesBasedOnSubStatusForGivenDates() throws Exception {
		when(subStatusService.employeesBasedOnSubStatusForGivenDates(new Date(), new Date(), "ClientLocation"))
				.thenReturn(null);

		mockMvc.perform(get("/employeesBasedOnSubStatusForGivenDates?subStatus=" + "ClientLocation")
				.param("fromDate", "2017-11-15").param("toDate", "2017-12-15")).andDo(print())
				.andExpect(status().isOk());

	}
}
