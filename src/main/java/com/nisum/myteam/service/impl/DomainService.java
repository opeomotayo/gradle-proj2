package com.nisum.myteam.service.impl;

import com.mongodb.WriteResult;
import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Domain;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.vo.DomainVO;
import com.nisum.myteam.repository.DomainRepo;
import com.nisum.myteam.service.IAccountService;
import com.nisum.myteam.service.IDomainService;
import com.nisum.myteam.service.IEmployeeRoleService;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IRoleService;
import com.nisum.myteam.utils.CommomUtil;
import com.nisum.myteam.utils.MyTeamUtils;
import com.nisum.myteam.utils.constants.ApplicationRole;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author Vijay
 *
 */
@Service
@Slf4j
public class DomainService implements IDomainService {

	@Autowired
	private DomainRepo domainRepo;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private IRoleService roleService;
	@Autowired
	private IEmployeeService employeeService;

	@Autowired
	private IEmployeeRoleService empRoleService;

	@Autowired
	private IAccountService accountService;


	public boolean isDomainExists(Domain domainReq) {
		boolean isDomainExists = false;
		int count = 0;
		List<Domain> domainListByName = domainRepo.findByDomainIdAndAccountId(domainReq.getDomainId(),
				domainReq.getAccountId());
		List<Domain> domainListbyAccountId = domainRepo.findByAccountId(domainReq.getAccountId());

		// Case sensitive logic
		for (Domain domainItr : domainListbyAccountId) {
			if (domainItr.getDomainName().equalsIgnoreCase(domainReq.getDomainName()))
				count++;
		}
		if (count > 0 || domainListByName.size() > 0) {
			isDomainExists = true;
		}
		return isDomainExists;
	}

	@Override
	public Domain create(Domain domainReq) throws MyTeamException {

		if (StringUtils.isEmpty(domainReq.getDomainId())) {
			domainReq.setDomainId((MyTeamUtils.DOM + MyTeamUtils.ZERO_) + (getDomainsList().size() + 1));
		}
		domainReq.setStatus(MyTeamUtils.ACTIVE);
		Domain domainPersisted = domainRepo.save(domainReq);
		if (domainPersisted != null) {
			saveEmployeeRole(domainReq);
		}
		return domainPersisted;
	}

	@Override
	public Domain update(Domain domainReq) throws MyTeamException {

		log.info("updating the roles for DeliveryManager in EmployeeRoleMapping collection");
		final String roleId = roleService.getRole(MyTeamUtils.DOMAIN);
		updateRoleIdsForDMs(domainReq, roleId);
		log.info("deleting roleids for DeliveryManagers in EmployeeRoleMapping collection");
		deleteRoleIdsForDMs(domainReq, roleId);

		log.info("updating the domain details");
		domainReq.setStatus(MyTeamUtils.ACTIVE);
		Domain domainPersisted = domainRepo.save(domainReq);

		log.info("After update the domain details::" + domainPersisted);

		return domainPersisted;
	}

	private void updateRoleIdsForDMs(Domain domainReq, String roleId) throws MyTeamException {

		Domain domainDAO = getDomainByStatusActive(domainReq.getDomainId());
		List dmssListDAO = domainDAO.getDeliveryManagers();
		List dmsListReq = domainReq.getDeliveryManagers();
		List<String> managersAddedByUser = managersAddedByUser = CommomUtil.getAddedManagersListForDM(dmssListDAO,
				dmsListReq);
		empRoleService.saveUniqueEmployeeAndRole(managersAddedByUser, roleId);

	}

	private void deleteRoleIdsForDMs(Domain domainUpdating, String roleId) throws MyTeamException {
		Map<String, Integer> managersDomainCount = new HashMap<String, Integer>();

		Domain domainDAO = getDomainByStatusActive(domainUpdating.getDomainId());
		List dmsListDAO = domainDAO.getDeliveryManagers();
		List dmsListReq = domainUpdating.getDeliveryManagers();

		List<String> managersDeletedByUser = CommomUtil.getDeletedManagersList(dmsListDAO, dmsListReq);

		List<Domain> domainsPersistedList = domainRepo.findAll();
		for (Domain domain : domainsPersistedList) {
			List employeeIds = domain.getDeliveryManagers();
			for (Object eId : employeeIds) {
				if (managersDomainCount.get(eId) != null)
					managersDomainCount.put(eId.toString(), managersDomainCount.get(eId) + 1);
				else
					managersDomainCount.put(eId.toString(), 1);
			}
		}

		for (String managerId : managersDeletedByUser) {
			if (managersDomainCount.get(managerId) == 1) {
				// Call service to delete manager
				empRoleService.deleteRole(managerId, roleId);
			}
		}
	}

	@Override
	public List<DomainVO> getDomainsList() throws MyTeamException {

		List<DomainVO> domainVOS = new ArrayList<>();

		for (Domain domainPersisted : domainRepo.findAll()) {
			DomainVO domainVO = new DomainVO();
			domainVO.setId(domainPersisted.getId().toString());
			domainVO.setAccountId(domainPersisted.getAccountId());
			domainVO.setAccountName(accountService.getAccountById(domainPersisted.getAccountId()).getAccountName());
			domainVO.setDomainId(domainPersisted.getDomainId());
			domainVO.setDomainName(domainPersisted.getDomainName());
			domainVO.setStatus(domainPersisted.getStatus());
			domainVO.setDeliveryManagers(prepareEmployeeList(domainPersisted));

			domainVOS.add(domainVO);

		}

		return domainVOS;
	}

	public Domain getDomainById(String domainId) {
		Domain domain = null;
		if (domainId != null && domainId.length() > 0) {
			domain = domainRepo.findByDomainId(domainId);

		}
		return domain;
	}

	@Override
	public List<Domain> getDomainsUnderAccount(String accountId) throws MyTeamException {
		List<Domain> domains = domainRepo.findByAccountId(accountId);
		return domains;

	}


	@Override
	public WriteResult delete(String domainId) throws MyTeamException {
		List<String> domEmpIds = new ArrayList<String>();
		String roleId = roleService.getRole(MyTeamUtils.DOMAIN);
		List<Domain> domainsPersistedList = domainRepo.findAll();
		Query selectedDomainQuery = new Query(
				Criteria.where(MyTeamUtils.DOMAIN_ID).in(domainId).and(MyTeamUtils.STATUS).in(MyTeamUtils.ACTIVE));
		List<Domain> selectedDomain = mongoTemplate.find(selectedDomainQuery, Domain.class);

		for (Domain domain : domainsPersistedList) {
			List employeeIds = domain.getDeliveryManagers();
			for (Object eIds : employeeIds)
				domEmpIds.add(eIds.toString());
		}

		for (Object empId : selectedDomain.get(0).getDeliveryManagers()) {
			int occurrences = Collections.frequency(domEmpIds, empId.toString());
			if (occurrences == 1) {
				// Service call for RoleMapping
				empRoleService.deleteRole(empId.toString(), roleId);
			}
		}
		Update update = new Update();
		update.set(MyTeamUtils.STATUS, MyTeamUtils.IN_ACTIVE);
		return mongoTemplate.upsert(selectedDomainQuery, update, Domain.class);
	}

	private void saveEmployeeRole(Domain domainReq) throws MyTeamException {
		String roleId = roleService.getRole(MyTeamUtils.DOMAIN);
		List dmsList = domainReq.getDeliveryManagers();
		empRoleService.saveUniqueEmployeeAndRole(dmsList, roleId);
	}

	private List<HashMap<String, String>> prepareEmployeeList(Domain domainPersisted) {
		ArrayList<HashMap<String, String>> empList = new ArrayList<HashMap<String, String>>();

		Query query = new Query(Criteria.where(MyTeamUtils.EMPLOYEE_ID).in(domainPersisted.getDeliveryManagers()));
		List<Employee> employeeList = mongoTemplate.find(query, Employee.class);
		for (Employee employee : employeeList) {
			HashMap<String, String> employeeMap = new HashMap<>();
			employeeMap.put(MyTeamUtils.EMPLOYEE_ID, employee.getEmployeeId());
			employeeMap.put(MyTeamUtils.EMPLOYEE_NAME, employee.getEmployeeName());
			empList.add(employeeMap);
		}
		return empList;
	}

	// Custom methods
	private Domain getDomainByStatusActive(String domainId) {
		Query selectedDomainQuery = new Query(
				Criteria.where(MyTeamUtils.DOMAIN_ID).in(domainId).and(MyTeamUtils.STATUS).in(MyTeamUtils.ACTIVE));
		List<Domain> selectedDomain = mongoTemplate.find(selectedDomainQuery, Domain.class);
		if (selectedDomain.size() == 1)
			return selectedDomain.get(0);
		return null;
	}

	
	private boolean duplicateCheck(String domainName, String accountId, List<String> fromDB, List fromUser) {
		boolean check = false;
		int count = 0;
		List<Domain> domainListbyAccountId = domainRepo.findByAccountId(accountId);
		boolean deleveryManagersCheck = fromDB.toString().contentEquals(fromUser.toString());
		List<Domain> domainList = domainRepo.findByDomainNameAndAccountId(domainName, accountId);
		for (Domain domains : domainListbyAccountId) {
			if (domains.getDomainName().equalsIgnoreCase(domainName))
				count++;
		}
		if ((domainList.size() > 0 && deleveryManagersCheck) || count > 1)
			check = true;
		return check;
	}

	@Override
	public Set<String> accountsAssignedToDeliveryLead(String empId) {
		Set<String> accIdsSet = new HashSet<String>();
		List<Domain> domainList = domainRepo.findByDeliveryManagers(empId);
		if (null != domainList && !domainList.isEmpty() && domainList.size() > MyTeamUtils.INT_ZERO) {
			for (Domain domain : domainList) {
				accIdsSet.add(domain.getAccountId());
			}
		}
		return accIdsSet;
	}

	@Override
	public List<DomainVO> getDomainsListByLoginId(String loginId) {
		List<DomainVO> domainVOS = new ArrayList<>();
		boolean isAdmin=employeeService.getEmployeeById(loginId).getRole().equals(ApplicationRole.ADMIN.getRoleName());

	 domainRepo.findAll().stream().filter(e-> e.getDeliveryManagers().stream().anyMatch(empId->empId.equals(loginId))||isAdmin).forEach(domainPersisted->
		 {
			DomainVO domainVO = new DomainVO();
			domainVO.setId(domainPersisted.getId().toString());
			domainVO.setAccountId(domainPersisted.getAccountId());
			domainVO.setAccountName(accountService.getAccountById(domainPersisted.getAccountId()).getAccountName());
			domainVO.setDomainId(domainPersisted.getDomainId());
			domainVO.setDomainName(domainPersisted.getDomainName());
			domainVO.setStatus(domainPersisted.getStatus());
			domainVO.setDeliveryManagers(prepareEmployeeList(domainPersisted));
	        domainVOS.add(domainVO);
		});

		return domainVOS;
	}
	

	

}