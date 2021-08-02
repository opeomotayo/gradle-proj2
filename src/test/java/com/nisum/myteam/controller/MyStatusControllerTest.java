package com.nisum.myteam.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.myteam.model.FromToDates;
import com.nisum.myteam.model.dao.MyStatus;
import com.nisum.myteam.service.MyStatusService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MyStatusControllerTest {

    @Mock
    private MyStatusService myStatusService;

    @InjectMocks
    private MyStatusController myStatusController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(myStatusController).build();
    }

    @Test
    public void testSaveStatusDetails() throws Exception {
        MyStatus status = getMyStatus();
        when(myStatusService.saveStatusDetails(status)).thenReturn(status);
        String jsonRequest = (new ObjectMapper()).writeValueAsString(status);
        mockMvc.perform(post("/saveStatusDetails").contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetDefalutStatusDetails() throws Exception {
        MyStatus status = getMyStatus();
        List<MyStatus> myStatus = new ArrayList<>();
        myStatus.add(status);
        when(myStatusService.getDefaultStatusDetails("40202")).thenReturn(myStatus);

        String response = (new ObjectMapper()).writeValueAsString(status);
        MvcResult mvcResult = mockMvc.perform(get("/getDefaultStatusDetails/empId", "40202")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        /*List<MyStatus> res = (new ObjectMapper()).readValue(
                mvcResult.getResponse().getContentAsByteArray(),
                new TypeReference<List<MyStatus>>(){});*/
        assertEquals(mvcResult.getResponse().getStatus(), 200);
    }

    @Test
    public void testFindTodayStatus() throws Exception {
        List<MyStatus> myStatus = new ArrayList<>();
        myStatus.add(getMyStatus());
        String response = (new ObjectMapper()).writeValueAsString(myStatus);

        MvcResult mvcResult = mockMvc.perform(get("/findTodayStatusByEmpId/empId", "40202")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 200);
    }

    @Test
    public void testDeleteEmployeeStatusDetails() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/deleteStatusDetails/10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 200);
    }

    @Test
    public void testUpdateEmployeeStatusDetails() throws Exception {
        MyStatus status = getMyStatus();
        status.setTaskDetails("Update-Task");
        String request = (new ObjectMapper()).writeValueAsString(status);
        MvcResult result = mockMvc.perform(put("/updateStatusDetails/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.taskDetails").value("Update-Task"))
                .andReturn();

    }


    public MyStatus getMyStatus() {
        MyStatus myStatus = new MyStatus();
        myStatus.setId(10);
        myStatus.setTaskDate(new Date());
        myStatus.setTicketNumber("PTOM-3224");
        myStatus.setStoryPoints(1);
        myStatus.setPlanedStartDate(new Date());
        myStatus.setPlanedEndDate(new Date());
        myStatus.setActualStartDate(new Date());
        myStatus.setActualEndDate(new Date());
        myStatus.setHoursSpent("07:00");
        myStatus.setTaskDetails("Add-Status");
        myStatus.setTaskType("Jira");
        myStatus.setPriority("Low");
        myStatus.setStatus("ToDo");
        myStatus.setEmpId("40202");
        myStatus.setComments("Started working on it");
        myStatus.setTaskAddedTime(LocalDateTime.now());
        return myStatus;
    }
}