package com.nisum.myteam.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.nisum.myteam.controller.ReportsController;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IResourceService;

public class ReportsControllerTest {
	
	@Mock
	IEmployeeService employeeService;

	@Mock
	IResourceService resourceService;
	
	@InjectMocks
	ReportsController reportsController;
	
	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(reportsController).build();
	}
	
	@Test
	public void testGetEmployeesByFunctionalGroup() throws Exception {
//		mockMvc.perform(get("/getEmployeesByFunctionalGroup1").param("domainId", "ACC001"))
//        .andExpect(MockMvcResultMatchers.status().isOk());
       // verify(masterDataService).getMasterData(); 
	}
}
