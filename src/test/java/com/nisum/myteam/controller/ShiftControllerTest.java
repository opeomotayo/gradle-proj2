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

import com.nisum.myteam.controller.ShiftController;
import com.nisum.myteam.service.IShiftService;

public class ShiftControllerTest {

	@Mock
    IShiftService shiftService;
	
	@InjectMocks
	ShiftController shiftController;
	
	private MockMvc mockMvc;
	
	 @Before
	    public void setup() {
	        MockitoAnnotations.initMocks(this);
	        mockMvc = MockMvcBuilders.standaloneSetup(shiftController).build();
	    }
	 
	 @Test
	    public void testgetAllShifts() throws Exception {
		// List<String> shiftsList = shiftList();
		// List<Shift> Response=null;
		 // when(shiftService.getAllShifts()).thenReturn(Response);
	 mockMvc.perform(get("/employees/shifts/").param("domainId", "ACC001"))
     .andExpect(MockMvcResultMatchers.status().isOk());
     verify(shiftService).getAllShifts(); 
	 }
	 
}
