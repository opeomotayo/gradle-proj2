package com.nisum.myteam.controller;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.nisum.myteam.controller.EmployeeEffortsController;
import com.nisum.myteam.model.EmployeeEfforts;
import com.nisum.myteam.service.IEmployeeEffortsService;

public class EmployeeEffortsControllerTest {
	@Mock
	IEmployeeEffortsService employeeEffortService;

	@InjectMocks
	EmployeeEffortsController employeeEffortsController;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(employeeEffortsController).build();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testemployeeLoginsBasedOnDateEfforts() throws Exception {
		List<EmployeeEfforts> employeeEffortsList = createLoginData();
		when(employeeEffortService.getEmployeeEffortsReport("2017-11-18", "2017-12-18")).thenReturn(employeeEffortsList);
		//when(employeeEffortService.getEmployeeEffortsReport("2017-11-19", "2017-12-19")).thenThrow(Exception.class);
		mockMvc.perform(get("/employeeEfforts/getWeeklyReport").param("fromDate", "2017-11-18")
				.param("toDate", "2017-12-18")).andDo(print()).andExpect(status().isOk());
		verify(employeeEffortService).getEmployeeEffortsReport("2017-11-18", "2017-12-18");
		}
	
/*	@SuppressWarnings("unchecked")
	@Test
	public void testemployeeLoginsBasedOnDateEffortsNegative() throws Exception {
		List<EmployeeEfforts> employeeEffortsList = createLoginData();
		when(employeeEffortService.getEmployeeEffortsReport(null, "2017-12-18")).thenThrow(new Exception());
	//	doThrow(new Exception()).when(employeeEffortService.getEmployeeEffortsReport("", "2017-12-19"));
		//when(employeeEffortService.getEmployeeEffortsReport("", "2017-12-19")).thenThrow(Exception.class);
		mockMvc.perform(get("/employeeEfforts/getWeeklyReport").param("fromDate", "")
				.param("toDate", "2017-12-18")).andExpect(status().isOk());
		//verify(employeeEffortService).getEmployeeEffortsReport("", "2017-12-18");
		}
	*/
		
		private List<EmployeeEfforts> createLoginData() {
			List<EmployeeEfforts> data = new ArrayList<>();

			EmployeeEfforts data1 = new EmployeeEfforts();
			data1.setId("5976ef15874c902c98b8a05b");
			data1.setEmployeeId("12345");
			data1.setEmployeeName("Xyz");
			data1.setTotalHoursSpentInWeek("55");
			data1.setProjectName("Macys");
			data1.setAccountName("Nisum");
			data1.setFunctionalOrg("Customer");

			EmployeeEfforts data2 = new EmployeeEfforts();
			data2.setId("5976ef15874c902c98b8a05c");
			data2.setEmployeeId("01234");
			data2.setEmployeeName("Abc");
			data2.setTotalHoursSpentInWeek("55");
			data2.setProjectName("Macys");
			data2.setAccountName("Nisum");
			data2.setFunctionalOrg("Customer");

			data.add(data1);
			data.add(data2);

			return data;
	}

}
