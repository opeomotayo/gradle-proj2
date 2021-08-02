package com.nisum.myteam.controller;
 
 import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
 import static org.mockito.Mockito.when;
 import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
 import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
 import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 
 import org.bson.types.ObjectId;
 import org.junit.Before;
 import org.junit.Test;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.MockitoAnnotations;
 import org.springframework.http.MediaType;
 import org.springframework.test.web.servlet.MockMvc;
 import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
 import org.springframework.test.web.servlet.setup.MockMvcBuilders;
 
 import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.myteam.controller.ProjectController;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.dao.Project;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IProjectService;

 
 public class ProjectControllerTest {
 
     @Mock
     IProjectService projectService;
 
     @Mock
     IEmployeeService employeeService;
 
//     @Mock
//     AccountRepo accountRepo;
 
     @InjectMocks
     ProjectController projectController;
 
     private MockMvc mockMvc;
 
     @Before
     public void setup() {
         MockitoAnnotations.initMocks(this);
         mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
     }
 
     @Test
     public void testgetEmployeeRole() throws Exception {
    	 Project employeePersisted = new Project();
    	 Project Project = new Project();
 		ObjectMapper mapper = new ObjectMapper();
 		String jsonString = mapper.writeValueAsString(Project);
 		when(projectService.updateProject(anyObject(), anyString())).thenReturn(Project);
 		mockMvc.perform(put("/projects/{projectId}","16253").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
 				.andExpect(MockMvcResultMatchers.status().isOk());
     }
     
   /*  @Test
     public void testgetEmployeeRole() throws Exception {
     	 EmployeeRoles employeesRole = new EmployeeRoles(
                  "5976ef15874c902c98b8a05d", null, null, "user@nisum.com", null,
                  null, null, null, null, null, null, null, null, null, null,
                  null, null, new Date(2017 - 11 - 12), new Date(2017 - 12 - 12),
                  null,
                  null, null,null,new Date(2020 - 01 - 01),new Date(2018 - 01 - 01),new Date(2018 - 02 - 15),new Date(2018 - 02 - 15),"Mahesh","Mahesh");
     	 when(userService.getEmployeesRole("user@nisum.com"))
          .thenReturn(employeesRole);
 		 mockMvc.perform(
 		         get("/project/employee").param("emailId", "user@nisum.com"))
 		         .andExpect(MockMvcResultMatchers.status().isOk());
 		 verify(userService).getEmployeesRole("user@nisum.com");
     }
 
     @Test
     public void testaddProject() throws Exception {
         List<String> list = new ArrayList<>();
          list.add("16620");
 
         Project employeeRole1 = new Project(
         		 new ObjectId("9976ef15874c902c98b8a05d"), "102","Macys", "NisumIndia","Active",list,
                  list, "Acc001", "DOM001",new Date(2018 - 06 - 29),
                  new Date(2018 - 12 - 20), list);
 
         Account account = new Account( "Acc001", "Macys", 3, "Y","Macys","Retail",list);
 
         when(projectService.addProject(employeeRole1))
                 .thenReturn(employeeRole1);
         when(accountRepo.findByAccountId("Acc001")).thenReturn(account);     
         String jsonvalue = (new ObjectMapper())
                 .writeValueAsString(employeeRole1).toString();
         mockMvc.perform(post("/project/addProject")
                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                 .content(jsonvalue))
                 .andExpect(MockMvcResultMatchers.status().isOk());
     }
 
     @Test
     public void testupdateEmployeeRole() throws Exception {
         List<String> employeeIds = new ArrayList<>();
         List<String> managerIds = new ArrayList<>();
         List<String> deliveryLeadIds = new ArrayList<>();
         
         employeeIds.add("16649");
         employeeIds.add("16650");
         employeeIds.add("16651");
         
         
         managerIds.add("16652");
         
         deliveryLeadIds.add("16653");
         deliveryLeadIds.add("16654");
         
         
         Project project = new Project(new ObjectId("5976ef15874c902c98b8a05d"),"Gap0026", 
         		"Mosaic", "Move", "Active", employeeIds, managerIds,"Acc002","DOM002"
         		,new Date(2017 - 11 - 29),
                 new Date(2017 - 12 - 20),deliveryLeadIds);
         ObjectMapper mapper = new ObjectMapper();
         String jsonString = mapper.writeValueAsString(project);
         when(projectService.updateProject(any())).thenReturn(project);
    }
 
     @Test
     public void testdeleteProject() throws Exception {
         mockMvc.perform(
                 delete("/project/deleteProject").param("projectId", "101"))
                 .andExpect(MockMvcResultMatchers.status().isOk());
         verify(projectService).deleteProject("101");
     }
 
     @Test
     public void testgetProjects() throws Exception {
     	List<HashMap<Object,Object>> projects = CreateProjectDetails();
         when(projectService.getProjects()).thenReturn(projects);
         mockMvc.perform(get("/project/getProjects")
                 .contentType(MediaType.APPLICATION_JSON_VALUE))
                 .andExpect(MockMvcResultMatchers.status().isOk());
     }
 
     @Test
     public void testgetEmployeeRoleData() throws Exception {
     	 EmployeeRoles employeesRole = new EmployeeRoles(
                  "5976ef15874c902c98b8a05d", null, null, null, null, null, null,
                  null, null, null, null, null, null, "user@nisum.com", null,
                  null, null, new Date(2017 - 11 - 20), new Date(2107 - 12 - 23),
                  null,
                  null, null,null,new Date(2020 - 01 - 01),new Date(2018 - 01 - 01),new Date(2018 - 02 - 15),new Date(2018 - 02 - 15),"Mahesh","Mahesh");
     	 when(userService.getEmployeesRoleData("16127")) .thenReturn(employeesRole);
         mockMvc.perform(
                 get("/project/getEmployeeRoleData").param("empId", "16127")
                         .contentType(MediaType.APPLICATION_JSON_VALUE))
                 .andExpect(MockMvcResultMatchers.status().isOk());
         verify(userService).getEmployeesRoleData("16127");
     }
 
     private List<HashMap<Object,Object>>CreateProjectDetails() {
     	List<HashMap<Object,Object>> data = new ArrayList<HashMap<Object,Object>> ();
     	HashMap<Object,Object> map1 = new HashMap<Object,Object>();
     	HashMap<Object,Object> map2 = new HashMap<Object,Object>();
     	
         Project data1 = new Project();
         data1.setId(new ObjectId("5976ef15874c902c98b8a05d"));
         data1.setProjectId("101");
         data1.setProjectName("MOSAIC");
         data1.setStatus("Billable");
         
         List<String> list = new ArrayList<>();
         list.add("16101");
         list.add("16102");
         list.add("16103");
         
         data1.setEmployeeIds(list);
         map1.put(new ObjectId("5976ef15874c902c98b8a05d"), data1);
 
         Project data2 = new Project();
         data2.setId(new ObjectId("9976ef15874c902c98b8a05d"));
         data2.setProjectId("102");
         data2.setProjectName("OMS");
         data2.setStatus("Non-Billable");
         List<String> lists = new ArrayList<>();
         lists.add("16104");
         lists.add("16105");
         lists.add("16106");
         data2.setEmployeeIds(lists);
         map2.put(new ObjectId("9976ef15874c902c98b8a05d"), data2);
 
         data.add(map1);
         data.add(map2);
 
         return data;
     } */
 }
