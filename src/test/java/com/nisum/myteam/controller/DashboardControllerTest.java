package com.nisum.myteam.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.nisum.myteam.controller.DashboardController;
import com.nisum.myteam.model.vo.EmployeeDashboardVO;
import com.nisum.myteam.service.IDashboardService;

public class DashboardControllerTest {
	@Mock
	IDashboardService dashboardService;

	@InjectMocks
	DashboardController dashboardController;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
	}

	@Test
	public void testGetEmployeesDashBoard() throws Exception {
		List<EmployeeDashboardVO> employeeDashBoardList = null;
		when(dashboardService.getEmployeesDashBoard()).thenReturn(employeeDashBoardList);
		mockMvc.perform(get("/resources/getEmployeesDashBoard").contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}
}
