package com.nisum.myteam.controller;

import java.util.Map;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.myteam.controller.AccountController;
import com.nisum.myteam.model.dao.Account;
import com.nisum.myteam.service.IAccountService;
import com.nisum.myteam.service.impl.AccountService;


public class AccountControllerTest {

	@Mock
	private IAccountService accountService;    
    @InjectMocks
    AccountController AccountController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(AccountController).build();
    }

    @Test    
    public void testCreateAccount() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("16620"); 
        list.add("16632");
        Account responce=null;
        Account account = new Account(
                new ObjectId("5b62b00950e71a6eecc8c98c"), "Acc003", "Nisum", 3,
                "Y","HYD","RetailS",list);
        when(accountService.createAccount(account))
                .thenReturn(responce);
        String jsonvalue = (new ObjectMapper())
                .writeValueAsString(account).toString();
        mockMvc.perform(post("/accounts").param("action","N")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonvalue))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test    
    public void testCreateAccountExists() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("16620"); 
        list.add("16632");
        Account responce=null;
        Account account = new Account(
                new ObjectId("5b62b00950e71a6eecc8c98c"), "Acc003", "Nisum", 3,
                "Y","HYD","RetailS",list);
        when(accountService.isAccountExists((Account)anyObject()))
        .thenReturn(true);
        when(accountService.createAccount(account))
                .thenReturn(responce);
        String jsonvalue = (new ObjectMapper())
                .writeValueAsString(account).toString();
        mockMvc.perform(post("/accounts").param("action","N")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonvalue))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test
    public void testgetAccountNames() throws Exception {
    	Account account=new Account();
    	account.setAccountId("123");
    	List<Account> accounts=new ArrayList<>();
    	accounts.add(account);
    	
    	//List<Map<Object, Object>> Account = CreateAccountDetails();
        when(accountService.getAllAccounts()).thenReturn(accounts);
        mockMvc.perform(get("/accounts/names")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
   /* 
    @Test
    public void testgetAccount() throws Exception {
    	List<Map<Object, Object>> Account = null;
        when(accountService.getAccountsList()).thenReturn(Account);
        mockMvc.perform(get("/accounts")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }  */
    @SuppressWarnings("unchecked")
	@Test
    public void testGetAccountsList() throws Exception {
    	List<String> list = new ArrayList<>();
        list.add("16620");
        list.add("16632");
       // String response=null; 
        Account accountPeristed=null;     
        Account account = new Account(new ObjectId("5976ef15874c902c98b8a05d"), "Acc003","Marketing",2,"Active","abx","dfv", list);
    	when(accountService.getAccountsList())
        .thenReturn((List<Map<Object, Object>>) accountPeristed);
    	 String jsonvalue = (new ObjectMapper()) 
                 .writeValueAsString(account).toString();
         mockMvc.perform(get("/accounts")
                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                 .content(jsonvalue))
                 .andExpect(MockMvcResultMatchers.status().isOk());
    } 
    
    @Test    
    public void testupdateAccount() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("16620");
        list.add("16632");
        Account responce=null;
        Account account = new Account(
                new ObjectId("5b62b00950e71a6eecc8c98c"), "Acc003", "Nisum", 3,
                "Y","HYD","RetailS",list);
        when(accountService.updateAccountAndRolesForDMS(account))
                .thenReturn(responce);
        String jsonvalue = (new ObjectMapper())
                .writeValueAsString(account).toString();
        mockMvc.perform(put("/accounts/Acc003").param("action","Acc003")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonvalue))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test    
    public void testupdateAccountExists() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("16620");
        list.add("16632");
        Account responce=null;
        Account account = new Account(
                new ObjectId("5b62b00950e71a6eecc8c98c"), "Acc003", "Nisum", 3,
                "Y","HYD","RetailS",list);
       when(accountService.isAccountExists((Account)anyObject())).thenReturn(true);
        when(accountService.updateAccountAndRolesForDMS(account))
                .thenReturn(account); 
        String jsonvalue = (new ObjectMapper())
                .writeValueAsString(account).toString();
        mockMvc.perform(put("/accounts/Acc003").param("action","Acc003")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonvalue))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test
    public void testdeleteAccount() throws Exception {
        mockMvc.perform(
                delete("/accounts/Acc002").param("accountId", "Acc002"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        //verify(AccountController).deleteAccount("Acc002");
    }
  
    @Test
    public void testGetALLAccounts() throws Exception {
    	List<Account> Account = null;
        when(accountService.getAllAccounts()).thenReturn(Account);
        mockMvc.perform(get("/accounts/status")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
   
    @Test    
    public void testGetAccountById() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("16620");
        list.add("16632");
        Account responce=null;
        Account account = new Account(
                new ObjectId("5b62b00950e71a6eecc8c98c"), "Acc003", "Nisum", 3,
                "Y","HYD","RetailS",list);
        when(accountService.getAccountById("Acc003"))
                .thenReturn(responce);
        String jsonvalue = (new ObjectMapper())
                .writeValueAsString(account).toString();
        mockMvc.perform(get("/accounts/accountId/Acc003").param("action","Acc003")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonvalue))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test    
    public void testGetAccountByIdExists() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("16620");
        list.add("16632");
        Account responce=null;
        Account account = new Account(
                new ObjectId("5b62b00950e71a6eecc8c98c"), "Acc003", "Nisum", 3,
                "Y","HYD","RetailS",list);
        when(accountService.isAccountExists("Acc003"))
        .thenReturn(true);
        when(accountService.getAccountById("Acc003"))
                .thenReturn(responce);
        String jsonvalue = (new ObjectMapper())
                .writeValueAsString(account).toString();
        mockMvc.perform(get("/accounts/accountId/Acc003").param("action","Acc003")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonvalue))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    private List<Map<Object,Object>>CreateAccountDetails() {
    	List<Map<Object,Object>> data = new ArrayList<Map<Object,Object>> ();
    	HashMap<Object,Object> map1 = new HashMap<Object,Object>();
    	HashMap<Object,Object> map2 = new HashMap<Object,Object>();
        Account data1 = new Account();
        data1.setId(new ObjectId("5976ef15874c902c98b8a05d"));
        data1.setAccountId("Acc004");
        data1.setAccountName("Govt");
        data1.setAccountProjectSequence(4);
        data1.setClientAddress("BNG");
        data1.setIndustryType("Telecom");
        data1.setStatus("Y");
        List<String> list = new ArrayList<>();
        list.add("16101");
        list.add("16102");
        list.add("16103");
        
        data1.setDeliveryManagers(list);
     	
        Account data2 = new Account();
        data2.setId(new ObjectId("9976ef15874c902c98b8a05d"));
        data2.setAccountId("Acc004");
        data2.setAccountName("Govt");
        data2.setAccountProjectSequence(4);
        data2.setClientAddress("HYD");
        data2.setIndustryType("Telecom");
        data2.setStatus("Y");
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
