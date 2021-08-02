package com.nisum.myteam.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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

import com.nisum.myteam.controller.DesignationController;
import com.nisum.myteam.model.dao.Designation;
import com.nisum.myteam.service.IDesignationService;

public class DesignationControllerTest {
	@Mock
	IDesignationService designationService;

	@InjectMocks
	DesignationController designationController;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(designationController).build();
	}

	@Test
	public void testGetAllDesignations() throws Exception {

		mockMvc.perform(get("/employees/designations/").param("domainId", "ACC001"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(designationService).getAllDesignations();
	}
}
