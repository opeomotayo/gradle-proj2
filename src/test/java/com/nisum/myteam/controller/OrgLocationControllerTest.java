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

import com.nisum.myteam.controller.OrgLocationController;
import com.nisum.myteam.service.IOrgLocationService;


public class OrgLocationControllerTest {
	@Mock
	IOrgLocationService orgLocationService;
	
	@InjectMocks
	OrgLocationController orgLocationController;
	
	private MockMvc mockMvc;
	
	 @Before
	    public void setup() {
	        MockitoAnnotations.initMocks(this);
	        mockMvc = MockMvcBuilders.standaloneSetup(orgLocationController).build();
	    }
	 
	 @Test
	    public void testgetDomainsUnderAccount() throws Exception {
	    	/*
	    	List domains = CreateDomainDetails();
	        when(domainService.getDomainsList()).thenReturn(domains);
	        mockMvc.perform(get("/domains/{accountId}")
	                .contentType(MediaType.APPLICATION_JSON_VALUE))
	                .andExpect(MockMvcResultMatchers.status().isOk());
	        verify(domainService).getDomainsList();  */
	    	
	    	mockMvc.perform(get("/organization/locations/").param("domainId", "ACC001"))
	        .andExpect(MockMvcResultMatchers.status().isOk());
	        verify(orgLocationService).getLocations(); 
	    	
	    }
}
