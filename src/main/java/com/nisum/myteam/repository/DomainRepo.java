package com.nisum.myteam.repository;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.Domain;

public interface DomainRepo extends MongoRepository<Domain, String> {

	List<Domain> findByDomainNameAndAccountId(String domianName,String accountId);

	List<Domain> findByDomainIdAndAccountId(String domianId,String accountId);
	
	List<Domain> findByAccountId(String accountId);
	
	Domain findByDomainId(String domainId);
	
	List<Domain> findByDeliveryManagers(String empId);

} 