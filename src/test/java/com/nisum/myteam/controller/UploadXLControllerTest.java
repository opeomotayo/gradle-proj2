package com.nisum.myteam.controller;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.util.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.nisum.myteam.controller.UploadXLController;
import com.nisum.myteam.service.IUploadXLService;

public class UploadXLControllerTest {

	@Mock
	IUploadXLService uploadService;
	
	@InjectMocks
	UploadXLController uploadXLController;
	
	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(uploadXLController).build();
	}
	
	@Test
	public void testExportDataFromFile() throws Exception {
		//List list = new ArrayList();
		String result="resl";
		//MultipartFile file = null;
		 byte[] content=new byte[5];
		 File file = new File("src/test/java/input.txt");
		    FileInputStream input = new FileInputStream(file);
		 MultipartFile multipartFile = new MockMultipartFile("file",
		            file.getName(), "text/plain", IOUtils.toByteArray(input));
		when(uploadService.importDataFromExcelFile(multipartFile, "")).thenReturn(result);
		mockMvc.perform(post("/employee/fileUpload").contentType( MediaType.MULTIPART_FORM_DATA_VALUE)
	               .content(result))
				.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(content().string(result));
		//verify(uploadService).importDataFromExcelFile(file, "16253");
	
	}
	
	
}
