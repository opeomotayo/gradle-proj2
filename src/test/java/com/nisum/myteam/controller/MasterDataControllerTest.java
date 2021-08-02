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

import com.nisum.myteam.controller.MasterDataController;
import com.nisum.myteam.service.IMasterDataService;

public class MasterDataControllerTest {
	
	@Mock
	IMasterDataService masterDataService;
	
	@InjectMocks
	MasterDataController masterDataController;
	
	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(masterDataController).build();
	}
	
	@Test
	public void testGetMasterData() throws Exception {
		mockMvc.perform(get("/getMasterData").param("domainId", "ACC001"))
        .andExpect(MockMvcResultMatchers.status().isOk());
        verify(masterDataService).getMasterData(); 
	}
}
