package com.nisum.myteam.service.impl;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Account;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.repository.AccountRepo;
import com.nisum.myteam.service.IAccountService;
import com.nisum.myteam.service.IEmployeeRoleService;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IRoleService;
import com.nisum.myteam.utils.CommomUtil;
import com.nisum.myteam.utils.MyTeamUtils;
import com.nisum.myteam.utils.constants.ApplicationRole;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AccountService implements IAccountService {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IRoleService roleService;
    
    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private IEmployeeRoleService empRoleService;


    @Override
    public Account createAccount(Account accountReq) throws MyTeamException {

        accountReq.setAccountId(generateAccountId());
        accountReq.setStatus(MyTeamUtils.STRING_Y);
        accountReq.setAccountProjectSequence(new Integer(0));

        Account accountPersisted = accountRepo.save(accountReq);
        if (log.isInfoEnabled()) {
            log.info("Account has been persisted in database with account details::" + accountPersisted);
        }
        if (accountPersisted != null) {
            List<String> accountDmsList = accountReq.getDeliveryManagers();
            if (accountDmsList != null && !accountDmsList.isEmpty() && accountDmsList.size() > 0) {
                String roleId = roleService.getRole(MyTeamUtils.ACCOUNT);
                log.info("Going to add DM role id for account delivery managers::::" + accountDmsList);
                empRoleService.saveUniqueEmployeeAndRole(accountDmsList, roleId);
                log.info("Added roleids for delivery managers in rolemapping collection");
            }

        }
        return accountPersisted;
    }

    @Override
    public Account updateAccountAndRolesForDMS(Account accountUpdating) throws MyTeamException {

        Account accountBeforeUpdate = accountRepo.findByAccountId(accountUpdating.getAccountId());
        accountUpdating.setId(accountBeforeUpdate.getId());
        accountUpdating.setStatus(accountBeforeUpdate.getStatus());
        accountUpdating.setAccountProjectSequence(accountBeforeUpdate.getAccountProjectSequence());
        accountUpdating.setAccountName(accountUpdating.getAccountName().trim());

        log.info("Updating the roleids of DeliveryManagers in RoleMapping Collection");
        final String roleId = roleService.getRole(MyTeamUtils.ACCOUNT);
        updateRoleIdsForDeliveryManager(accountUpdating, roleId);

        log.info("Deleting the roleids of DeliveryManagers in RoleMapping Collection");
        deleteRoleIdsForDeliveryManager(accountUpdating, roleId);

        log.info("account updating::"+accountUpdating);
        Account accountUpdated = accountRepo.save(accountUpdating);

        log.info("Account updated::" + accountUpdated);
        return accountUpdated;
    }

    @Override
    public Account updateAccountSequence(Account account) {

        return accountRepo.save(account);
    }

    @Override
    public boolean isAccountExists(Account account) {

        Account accountFetched = accountRepo.findByAccountNameIgnoreCase(account.getAccountName());
        return (accountFetched != null) ? true : false;
    }

    @Override
    public boolean isAccountExists(String accountId) {
        boolean isExists = false;
        log.info("AccountId in Account Service::"+accountId);
        if (accountId != null && !"".equalsIgnoreCase(accountId)) {
            log.info("in condition");
           Account account= accountRepo.findByAccountId(accountId);
           log.info("Account details in Account Service::"+account);
            isExists = ( account== null) ? false : true;
        }

        log.info("isExists:"+isExists);
        return isExists;
    }

    @Override
    public Account getAccountById(String accountId) {
        log.info("AccountService::accountid:"+accountId);
        Account account=null;
        if (accountId != null && !"".equalsIgnoreCase(accountId)) {
             account = accountRepo.findByAccountId(accountId);
             log.info("AccountService::The account details are::"+account);
        }
        return account;
    }


    @Override
    public List<Account> getAllAccounts() throws MyTeamException {
        return accountRepo.findAll();
    }

    @Override
    public List<Map<Object, Object>> getAccountsList() throws MyTeamException {
        List<Map<Object, Object>> updatedAccountList = new ArrayList<>();
        List<Map<String, String>> updatedEmployeeList = null;
        for (Account account : accountRepo.findAll()) {
            updatedEmployeeList = new ArrayList<>();
            for (Employee employee : getEmployeeDetails(account)) {
                updatedEmployeeList.add(getEmployeeDetails(employee));
            }
            updatedAccountList.add(getAccuntDetails(account, updatedEmployeeList));
        }
        return updatedAccountList;
    }

    @Override
    public Account deleteAccount(String accountId) throws MyTeamException {

        // delete the documents for deliveryManagers in rolemapping collection.
        log.info("After updation:: Deleting the Roleids for DeliveryManagers in RoleMapping collection");
        deleteRoleidsForDeliveryManagers(accountId);

        // updating the status to "InActive".
        Query query = new Query(Criteria.where(MyTeamUtils.ACCOUNT_ID).is(accountId));
        Update update = new Update();
        update.set(MyTeamUtils.STATUS, MyTeamUtils.STRING_N);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);

        Account updatedAccount = mongoTemplate.findAndModify(query, update, options, Account.class);
        log.info("The account updated::" + updatedAccount);

        return updatedAccount;
    }

    @Override
    public Account getAccountByName(String name) {
        return accountRepo.findByAccountNameIgnoreCase(name);
    }

    // generating the account id.
    // accountId format is "Acc001"
    private String generateAccountId() throws MyTeamException {
        return (MyTeamUtils.ACC + MyTeamUtils.ZERO_) + (getAllAccounts().size() + MyTeamUtils.ONE);
    }

    private void updateRoleIdsForDeliveryManager(Account accountReq, String roleId) throws MyTeamException {

        List<String> updatingDmsList = accountReq.getDeliveryManagers();
        List<String> persistedDmsList = accountRepo.findByAccountId(accountReq.getAccountId()).getDeliveryManagers();

        List<String> dmsAddedByUser = CommomUtil.getAddedManagersList(persistedDmsList, updatingDmsList);
        empRoleService.saveUniqueEmployeeAndRole(dmsAddedByUser, roleId);
    }

    private void deleteRoleIdsForDeliveryManager(Account accountUpdating, String roleId) throws MyTeamException {
        List<String> dmIdList = null;
        Map<String, Integer> dmsCountMap = new HashMap<String, Integer>();

        List<String> updatingDmsList = accountUpdating.getDeliveryManagers();
        List<String> persistedDmsList = accountRepo.findByAccountId(accountUpdating.getAccountId())
                .getDeliveryManagers();
        List<String> dmsListDeletedByUser = CommomUtil.getDeletedManagersList(persistedDmsList, updatingDmsList);

        List<Account> allAccountList = accountRepo.findAll();
        if (allAccountList != null && !allAccountList.isEmpty() && allAccountList.size() > 0) {
            for (Account acc : allAccountList) {
                dmIdList = acc.getDeliveryManagers();
                if (dmIdList != null && !dmIdList.isEmpty() && dmIdList.size() > 0) {
                    for (String dmId : dmIdList) {
                        if (dmsCountMap.get(dmId) != null)
                            dmsCountMap.put(dmId, dmsCountMap.get(dmId) + 1);
                        else
                            dmsCountMap.put(dmId, 1);
                        dmIdList = null;
                    }
                }
            }
        }

        for (String empId : dmsListDeletedByUser) {
            if (dmsCountMap.get(empId) == 1) {
                // Service call for RoleMapping
                empRoleService.deleteRole(empId, roleId);
            }
        }
    }

    // fetching the employee details using employeeId.
    private List<Employee> getEmployeeDetails(Account account) {
        List<Employee> employeeRoles = mongoTemplate.find(
                new Query(Criteria.where(MyTeamUtils.EMPLOYEE_ID).in(account.getDeliveryManagers())),
                Employee.class);
        return employeeRoles;
    }

    private HashMap<String, String> getEmployeeDetails(Employee employeesRole) {
        HashMap<String, String> employeeDetails = new HashMap<>();
        employeeDetails.put(MyTeamUtils.EMPLOYEE_ID, employeesRole.getEmployeeId());
        employeeDetails.put(MyTeamUtils.EMPLOYEE_NAME, employeesRole.getEmployeeName());
        return employeeDetails;
    }

    private Map<Object, Object> getAccuntDetails(Account account, List<Map<String, String>> updatedEmployeeList) {
        Map<Object, Object> accountDetails = new HashMap<>();
        //accountDetails.put(MyTimeUtils.ID_, account.getId());
        accountDetails.put(MyTeamUtils.ACCOUNT_ID, account.getAccountId());
        accountDetails.put(MyTeamUtils.ACCOUNT_NAME, account.getAccountName());
        accountDetails.put(MyTeamUtils.STATUS, account.getStatus());
        accountDetails.put(MyTeamUtils.CLIENT_ADDRESS, account.getClientAddress());
        accountDetails.put(MyTeamUtils.INDUSTRY_TYPE, account.getIndustryType());
        accountDetails.put(MyTeamUtils.DELIVERY_MANAGERS, updatedEmployeeList);
        return accountDetails;
    }

    private void deleteRoleidsForDeliveryManagers(String accountId) throws MyTeamException {
        int occurrences = 0;
        List<Account> allAccountsList = null;
        List<String> accountDms = null;
        List<String> tempDmsList = new ArrayList<String>();

        String roleId = roleService.getRole(MyTeamUtils.ACCOUNT);
        allAccountsList = accountRepo.findAll();

        List<String> accountDmsList = accountRepo.findByAccountId(accountId).getDeliveryManagers();

        for (Account account : allAccountsList) {
            accountDms = account.getDeliveryManagers();

            for (String accountDm : accountDms)
                tempDmsList.add(accountDm);

            accountDms = null;
        }

        for (String dmId : accountDmsList) {
            occurrences = Collections.frequency(tempDmsList, dmId);
            if (occurrences == 1) {
                // Service call for RoleMapping
                empRoleService.deleteRole(dmId, roleId);
            }
        }
    }

	@Override
	public List<Map<Object, Object>> getAccountsListByLoginId(String loginId) {
		boolean isAdmin=employeeService.getEmployeeById(loginId).getRole().equals(ApplicationRole.ADMIN.getRoleName());

		
	//	List<Account> accounts
	    List<Map<Object, Object>> updatedAccountList = new ArrayList<>();
        List<Map<String, String>> updatedEmployeeList = null;
        for (Account account : accountRepo.findAll()) {
            updatedEmployeeList = new ArrayList<>();
            for (Employee employee : getEmployeeDetails(account)) {
             
            		updatedEmployeeList.add(getEmployeeDetails(employee));
            }
          if(updatedEmployeeList.stream().map(e->e.get(MyTeamUtils.EMPLOYEE_ID)).anyMatch(empId->empId.equals(loginId))||isAdmin)
              updatedAccountList.add(getAccuntDetails(account, updatedEmployeeList));
        }
        return updatedAccountList;
		
	}




}
