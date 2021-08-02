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

import com.nisum.myteam.controller.SkillController;
import com.nisum.myteam.service.ISkillService;

public class SkillControllerTest {
	@Mock
	ISkillService skillService;
	
	@InjectMocks
	SkillController skillController;
	
	private MockMvc mockMvc;
	
	 @Before
	    public void setup() {
	        MockitoAnnotations.initMocks(this);
	        mockMvc = MockMvcBuilders.standaloneSetup(skillController).build();
	    }
	 
	 @Test
	    public void testGetTechnologies() throws Exception {
		// List<String> shiftsList = shiftList();
		// List<Shift> Response=null;
		 // when(shiftService.getAllShifts()).thenReturn(Response);
	 mockMvc.perform(get("/employees/skills/").param("domainId", "ACC001"))
  .andExpect(MockMvcResultMatchers.status().isOk());
  verify(skillService).getTechnologies(); 
	 }
}
