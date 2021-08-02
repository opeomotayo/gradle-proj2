package com.nisum.myteam.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.ArrayList;
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
import com.nisum.myteam.controller.DomainController;
import com.nisum.myteam.model.dao.Domain;
import com.nisum.myteam.service.IDomainService;


public class DomainControllerTest {

	
    @Mock
    IDomainService domainService;

    @InjectMocks
    DomainController domainController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(domainController).build();
    }

    @Test    
    public void testCreateDomain() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("16620");
        list.add("16632");
       // String response=null; 
        Domain domainPeristed=null;
        
         
        Domain domains = new Domain( new ObjectId("5976ef15874c902c98b8a05d"), "DOM002", "Marketing", "Acc002","Active",list);

        when(domainService.create(domains))
                .thenReturn(domainPeristed);
        
        
        String jsonvalue = (new ObjectMapper()) 
                .writeValueAsString(domains).toString();
        mockMvc.perform(post("/domains")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonvalue))
                .andExpect(MockMvcResultMatchers.status().isOk());
 
    }
    
    @Test    
    public void testCreateDomainForDomainExist() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("16620");
        list.add("16632");
       // String response=null; 
        Domain domainPeristed=null;
        
        
        Domain domains = new Domain( new ObjectId("5976ef15874c902c98b8a05d"), "DOM002", "Marketing", "Acc002","Active",list);
        when(domainService.isDomainExists(anyObject()))
        .thenReturn(true);
        when(domainService.create(domains))
                .thenReturn(domainPeristed); 
        
        
        String jsonvalue = (new ObjectMapper()) 
                .writeValueAsString(domains).toString();
        mockMvc.perform(post("/domains")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonvalue))
                .andExpect(MockMvcResultMatchers.status().isOk());
 
    }
    

    @Test
    public void testupdateDomains() throws Exception {
        List<String> employeeIds = new ArrayList<>();       
        employeeIds.add("16649");
        employeeIds.add("16650");
       // employeeIds.add("16651");      
     //   String responce=null;
       Domain domainPeristed=null;
        Domain domain = new Domain( new ObjectId("9976ef15874c902c98b8a05d"), "DOM005", "Marketing", "ACC001", "Active",employeeIds);  
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(domain);
        when(domainService.update(any())).thenReturn(domainPeristed);
        String jsonvalue = (new ObjectMapper()) 
                .writeValueAsString(domain).toString();
       mockMvc.perform(put("/domains")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isOk());
       //verify(domainService).update(any());
    }
    
    @Test
    public void testupdateDomainsForDomainExists() throws Exception {
        List<String> employeeIds = new ArrayList<>();       
        employeeIds.add("16649");
        employeeIds.add("16650");
       // employeeIds.add("16651");      
     //   String responce=null;
       Domain domainPeristed=null;
        Domain domain = new Domain( new ObjectId("9976ef15874c902c98b8a05d"), "DOM005", "Marketing", "ACC001", "Active",employeeIds);  
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(domain);
        when(domainService.isDomainExists(anyObject()))
        .thenReturn(true);
        when(domainService.update(any())).thenReturn(domainPeristed);
       mockMvc.perform(put("/domains")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isOk());
       //verify(domainService).update(any());
    }
    
    @Test
    public void testgetDomains() throws Exception {
    	//List<HashMap<Object,Object>> domains = CreateDomainDetails();
    	List domains = CreateDomainDetails();
        when(domainService.getDomainsList()).thenReturn(domains);
        mockMvc.perform(get("/domains")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(domainService).getDomainsList();
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
    	
    	mockMvc.perform(get("/domains/ACC001").param("domainId", "ACC001"))
        .andExpect(MockMvcResultMatchers.status().isOk());
        verify(domainService).getDomainsUnderAccount("ACC001"); 
    	
    }
   

    @Test
    public void testdeleteDomain() throws Exception {
        mockMvc.perform(delete("/domains/DOM005").param("domainId", "DOM005"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(domainService).delete("DOM005"); 
    }

    
    private List<HashMap<Object,Object>>CreateDomainDetails() {
    	List<HashMap<Object,Object>> data = new ArrayList<HashMap<Object,Object>> ();
    	HashMap<Object,Object> map1 = new HashMap<Object,Object>();
    	HashMap<Object,Object> map2 = new HashMap<Object,Object>();
    	
        Domain data1 = new Domain();
        data1.setId(new ObjectId("5976ef15874c902c98b8a05d"));
        data1.setDomainId("DOM003");
        data1.setDomainName("MOC");
        data1.setAccountId("ACC001");
        data1.setStatus("Active");
        List<String> list = new ArrayList<>();
        list.add("16101");
        list.add("16102");
        list.add("16103");
        
        data1.setDeliveryManagers(list);
        map1.put(new ObjectId("5976ef15874c902c98b8a05d"), data1);

      	
        Domain data2 = new Domain();
        data2.setId(new ObjectId("9976ef15874c902c98b8a05d"));
        data2.setDomainId("DOM005");
        data2.setDomainName("BIGTICKET");
        data2.setAccountId("ACC001");
        data2.setStatus("Active");
        List<String> list2 = new ArrayList<>();
        list2.add("16103");
        list2.add("16105");
        list2.add("16107");
        
        data1.setDeliveryManagers(list);
        map1.put(new ObjectId("5976ef15874c902c98b8a05d"), data1);
        map2.put(new ObjectId("9976ef15874c902c98b8a05d"), data2);

        data.add(map1);
        data.add(map2);

        return data;
    }
    
}
