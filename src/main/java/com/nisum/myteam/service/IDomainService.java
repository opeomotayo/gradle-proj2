package com.nisum.myteam.service;

import java.util.List;
import java.util.Set;

import com.nisum.myteam.model.vo.DomainVO;
import org.springframework.stereotype.Service;

import com.mongodb.WriteResult;
import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Domain;

/**
 * @author Vijay
 *
 */
@Service
public interface IDomainService {

	boolean isDomainExists(Domain domainReq);

	Domain create(Domain domain) throws MyTeamException;

	// List<HashMap<Object,Object>> getAllDomains() throws MyTimeException;

	Domain update(Domain domain) throws MyTeamException;

	WriteResult delete(String id) throws MyTeamException;

	List<DomainVO> getDomainsList() throws MyTeamException;
	
	Set<String> accountsAssignedToDeliveryLead(String empId) throws MyTeamException;
	
	 List<Domain> getDomainsUnderAccount(String accountId)throws MyTeamException;
	 
	 Domain getDomainById(String domainId);

	List<DomainVO> getDomainsListByLoginId(String loginId);

			 
}
