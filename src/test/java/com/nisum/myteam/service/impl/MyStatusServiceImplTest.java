package com.nisum.myteam.service.impl;

import com.nisum.myteam.model.dao.MyStatus;
import com.nisum.myteam.repository.MyStatusRepository;
import com.nisum.myteam.repository.MyStatusRepositoryImpl;
import com.nisum.myteam.service.MyStatusService;
import com.nisum.myteam.service.SequenceGeneratorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MyStatusServiceImplTest {

    @InjectMocks
    private MyStatusServiceImpl myStatusService;

    @Mock
    private MyStatusRepositoryImpl myStatusRepositoryImpl;

    @Mock
    private MyStatusRepository myStatusRepository;

    private MockMvc mockMvc;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        //mockMvc = MockMvcBuilders.standaloneSetup(myStatusService).build();
    }

    @Test
    public void saveStatusDetails() {
        MyStatus status = getMyStatus();
        when(myStatusRepository.save(status)).thenReturn(status);
        assertEquals(status, myStatusService.saveStatusDetails(status));
    }

    @Test
    public void testGetDefaultStatusDetails() {
        List<MyStatus> statusList = new ArrayList<>();
        statusList.add(getMyStatus());
        when(myStatusRepositoryImpl.findByLastFiveDays("40202")).thenReturn(statusList);
        List<MyStatus> status = myStatusService.getDefaultStatusDetails("40202");
        assertEquals(statusList, status);
    }

    @Test
    public void testFindTodayStatus() {
        List<MyStatus> statusList = new ArrayList<>();
        statusList.add(getMyStatus());
        when(myStatusRepositoryImpl.findTodayStatus("40202", LocalDate.now())).thenReturn(statusList);
        List<MyStatus> status = myStatusService.findTodayStatus("40202");
        assertEquals(statusList, status);
    }

    @Test
    public void testGetByDateRange() {
        MyStatus status = getMyStatus();
        List<MyStatus> statusList = new ArrayList<>();
        statusList.add(status);
        //statusList.add(status);
        when(myStatusRepositoryImpl.findByDateRange(new Date(), new Date(), "40202")).thenReturn(statusList);
        List<MyStatus> result = myStatusService.getByDateRange(new Date(), new Date(), "40202");
        assertEquals(statusList, result);
    }

    @Test
    public void testFindTaskDataByTaskDate() {
        MyStatus status = getMyStatus();
        List<MyStatus> statusList = new ArrayList<>();
        statusList.add(status);
        statusList.add(status);
        when(myStatusRepositoryImpl.findTodayStatus("40202", LocalDate.now())).thenReturn(statusList);
        List<MyStatus> result = myStatusService.findTaskDataByTaskDate("40202", LocalDate.now());
        assertEquals(statusList, result);
        assertEquals(statusList.size(), result.size());
    }

    @Test
    public void testDeleteEmployeeStatusDetails() {
        MyStatus status = getMyStatus();
        when(myStatusRepository.existsById(status.getId())).thenReturn(true);
        assertFalse(myStatusRepository.exists(status.getId()));
    }

    @Test
    public void testUpdateStatusDetails() {
        MyStatus status = getMyStatus();

        when(myStatusRepository.findById(40202)).thenReturn(Optional.ofNullable(status));
        status.setTaskDetails("Update-Task");

        when(myStatusRepository.save(status)).thenReturn(status);
        assertEquals(status.getTaskDetails(), "Update-Task");
        //MyStatus result = myStatusService.updateStatusDetails(status);
        //assertEquals(status.getTaskDetails(), result.getTaskDetails());

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