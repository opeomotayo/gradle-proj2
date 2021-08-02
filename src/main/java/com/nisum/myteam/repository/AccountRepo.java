package com.nisum.myteam.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.Account;

public interface AccountRepo extends MongoRepository<Account, String> {
	
	Account findByAccountName(String accontName);
	
	Account  findByAccountId(String accountId);
	
	List<Account> findByaccountNameAndAccountId(String accountName,String accountId);
	
	Account findByAccountNameIgnoreCase(String accountName);
}
