package com.nisum.myteam.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.nisum.myteam.controller.EmployeeLocationController;
import com.nisum.myteam.model.dao.EmployeeLocation;
import com.nisum.myteam.service.IEmployeeLocationService;


public class EmployeeLocationControllerTest {

	@Mock
	IEmployeeLocationService empLocationService;

	@InjectMocks
	EmployeeLocationController employeeLocationController;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(employeeLocationController).build();
	}
	
	 @Test
	    public void testgetEmployeeLocations() throws Exception {
		 List<EmployeeLocation> employeeLocationDetails = createEmplyeeLocationData();
		 when(empLocationService.getEmployeeLocations("16127")).thenReturn(employeeLocationDetails);
		 mockMvc.perform(get("/employees/locations/16127"))
			.andExpect(MockMvcResultMatchers.status().isOk());
	verify(empLocationService).getEmployeeLocations("16127");
	 }

	private List<EmployeeLocation> createEmplyeeLocationData() {
		List<EmployeeLocation> data = new ArrayList<>();

		EmployeeLocation record1 = new EmployeeLocation();
		record1.setEmployeeId("16127");
		record1.setEmployeeName("Monika Srivastava");
		
		EmployeeLocation record2 = new EmployeeLocation();
		record2.setEmployeeId("16157");
		record2.setEmployeeName("Syed Parveen");
		
		data.add(record1);
		data.add(record2);

		return data;
}
}
