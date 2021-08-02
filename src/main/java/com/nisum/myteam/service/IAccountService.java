package com.nisum.myteam.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Account;

@Service
public interface IAccountService {

	Account createAccount(Account account) throws MyTeamException;
	
	Account updateAccountAndRolesForDMS(Account account) throws MyTeamException;

	public Account updateAccountSequence(Account account);

	boolean isAccountExists(Account account);

	boolean isAccountExists(String accountId);

	public Account getAccountById(String accountId);
	
	List<Account> getAllAccounts() throws MyTeamException;

	List<Map<Object, Object>> getAccountsList() throws MyTeamException;

	Account deleteAccount(String accountId) throws MyTeamException;

	public Account getAccountByName(String name);

	List<Map<Object, Object>> getAccountsListByLoginId(String loginId);

}
