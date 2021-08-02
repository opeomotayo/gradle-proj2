package com.nisum.myteam.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.nisum.myteam.controller.AttendanceController;
import com.nisum.myteam.model.AttendenceData;
import com.nisum.myteam.model.dao.EmpLoginData;
import com.nisum.myteam.service.IAttendanceService;

public class AttendanceControllerTest {


	@Mock
	IAttendanceService attendanceService;

	@InjectMocks
	AttendanceController attendanceController;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(attendanceController).build();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testemployeeLoginsBasedOnDate() throws Exception {
		List<EmpLoginData> message = createLoginData();
		when(attendanceService.employeeLoginsBasedOnDate(12345, "2017-11-15", "2017-12-15")).thenReturn(message);
		when(attendanceService.employeeLoginsBasedOnDate(01234, "2017-11-15", "2017-12-15")).thenThrow(Exception.class);
		mockMvc.perform(get("/attendance/employeeLoginsBasedOnDate?empId=" + 12345).param("fromDate", "2017-11-15")
				.param("toDate", "2017-12-15")).andDo(print()).andExpect(status().isOk());
		}
	

	@Test
	public void testgeneratePdfReport() throws Exception {
		List list = new ArrayList();
		when(attendanceService.generatePdfReport(12345, "2017-11-18", "2017-12-18")).thenReturn(list);
		mockMvc.perform(get("/attendance/generatePdfReport/12345/2017-11-18/2017-12-18"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(attendanceService).generatePdfReport(12345, "2017-11-18", "2017-12-18");
	}
	
	@Test
	public void testgeneratePdfReportWithTime() throws Exception {
		List list = new ArrayList();
		when(attendanceService.generatePdfReport(12345, "2017-11-18", "2017-12-18", "12:12:04","15:12:04")).thenReturn(list);
		mockMvc.perform(get("/attendance/generatePdfReport/12345/2017-11-18/2017-12-18/12:12:04/15:12:04"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(attendanceService).generatePdfReport(12345, "2017-11-18", "2017-12-18", "12:12:04","15:12:04");
	}
	

	@Test
	public void testattendanciesReport() throws Exception {
		List<AttendenceData> lisOfAttendenceData = createAttendenceData();
		when(attendanceService.getAttendanciesReport("2017-12-29", "All")).thenReturn(lisOfAttendenceData);
		mockMvc.perform(get("/attendance/attendanciesReport/2017-12-29").param("shift", "All"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(attendanceService).getAttendanciesReport("2017-12-29", "All");
	}

	@Test
	public void testemployeesDataSave() throws Exception {
		when(attendanceService.fetchEmployeesData("2018-01-01", false)).thenReturn(true);
		mockMvc.perform(post("/attendance/employeesDataSave/2018-01-01"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(attendanceService).fetchEmployeesData("2018-01-01", false);
	}
	
	@Test
	public void testResyncMonthData() throws Exception {
		when(attendanceService.fetchEmployeesData("2019-01-02", true)).thenReturn(true);
		mockMvc.perform(post("/attendance/resyncMonthData/2019-01-02"))
		.andExpect(MockMvcResultMatchers.status().isOk());
		verify(attendanceService).fetchEmployeesData("2019-01-02", true);	
	}

	private List<AttendenceData> createAttendenceData() {
		List<AttendenceData> data = new ArrayList<>();

		AttendenceData record1 = new AttendenceData();
		record1.setEmployeeId("16127");
		record1.setEmployeeName("Monika Srivastava");
		record1.setPresent("Present");
		record1.setTotalAbsent(0);
		record1.setTotalPresent(31);

		AttendenceData record2 = new AttendenceData();
		record2.setEmployeeId("16157");
		record2.setEmployeeName("Syed Parveen");
		record2.setPresent("Present");
		record2.setTotalAbsent(0);
		record2.setTotalPresent(30);

		data.add(record1);
		data.add(record2);

		return data;
	}

	private List<EmpLoginData> createLoginData() {
		List<EmpLoginData> data = new ArrayList<>();

		EmpLoginData data1 = new EmpLoginData();
		data1.setId("5976ef15874c902c98b8a05b");
		data1.setEmployeeId("12345");
		data1.setEmployeeName("Xyz");
		data1.setDateOfLogin("2017-12-14");
		data1.setFirstLogin("08:30");
		data1.setLastLogout("05:30");
		data1.setTotalLoginTime("09:00");
		data1.setDirection("1");
		data1.setTotalAvgTime("08:28");

		EmpLoginData data2 = new EmpLoginData();
		data2.setId("5976ef15874c902c98b8a05c");
		data2.setEmployeeId("01234");
		data2.setEmployeeName("Abc");
		data2.setDateOfLogin("2017-12-15");
		data2.setFirstLogin("09:30");
		data2.setLastLogout("06:30");
		data2.setTotalLoginTime("09:00");
		data2.setDirection("2");
		data2.setTotalAvgTime("07:51");

		data.add(data1);
		data.add(data2);

		return data;
	}

}
