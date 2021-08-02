package com.nisum.myteam.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class PropertyControllerTest {
	
	@InjectMocks
	PropertyController propertyController;
	
	private MockMvc mockMvc; 

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(propertyController).build();
	}
	
	@Test
	public void testgetProperties() throws Exception {
		HashMap<String, Object> configMap = new HashMap<>();
		MockHttpServletRequest msr=new MockHttpServletRequest();
		MvcResult mvc =(MvcResult) ((MvcResult) msr).getResponse();
		configMap.put("message", "o");
		mockMvc.perform(get("message")).andReturn();
		//propertyController.getProperties();
	}

}
