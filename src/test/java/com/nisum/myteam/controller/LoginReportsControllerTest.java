package com.nisum.myteam.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.nisum.myteam.controller.LoginReportsController;
import com.nisum.myteam.service.ILoginReportsService;

public class LoginReportsControllerTest {
	@Mock
	ILoginReportsService reportsService;

	@InjectMocks
	LoginReportsController loginReportsController;

	private MockMvc mockMvc;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(loginReportsController).build();
	}

	
/*	@Test
	public void testdeletePdfReport() throws Exception {
		String response = "Success!!";
		when(reportsService.deletePdfReport("LoginData")).thenReturn(response);
		mockMvc.perform(post("/deleteReport/LoginData"))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(null);
		verify(reportsService).deletePdfReport("LoginData");	 
	} */
	@Test    
	   public void testdeletePdfReport() throws Exception {
	   	String fileName="MyTime";
	       String response="hh";
	       when( reportsService.deletePdfReport(fileName))
	               .thenReturn(response);
	       mockMvc.perform(get("/deleteReport/{fileName}",fileName).contentType( MediaType.TEXT_PLAIN_VALUE)
	               .content(fileName))
	               .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(content().string("hh"));
	       verify(reportsService,times(1)).deletePdfReport(fileName);
	       verifyNoMoreInteractions(reportsService);
	   }
}
