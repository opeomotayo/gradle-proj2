package com.nisum.myteam.service.impl;

import com.nisum.myteam.model.dao.EmployeeShift;
import com.nisum.myteam.repository.EmployeeShiftRepo;
import com.nisum.myteam.service.IEmployeeShiftService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EmployeeShiftService implements IEmployeeShiftService {

    @Autowired
    private EmployeeShiftRepo empShiftsRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

//	@Override
//	public void addEmployeeShift(Resource resource, String loginEmpId) {
//		EmployeeShift empShift = new EmployeeShift();
//		empShift.setEmployeeName(resource.getEmployeeName());
//		empShift.setEmployeeId(resource.getEmployeeId());
//		empShift.setShift(resource.getShift());
//		empShift.setActive(resource.isActive());
//		empShift.setAuditFields(loginEmpId, MyTeamUtils.CREATE);
//		empShiftsRepo.save(empShift);
//		log.info("The Employee Shift has been Persisted ::" + empShift);
//	}

//	public void updateEmployeeShift(Resource resource, String loginEmpId) {
//		Query getQuery = new Query();
//		getQuery.addCriteria(new Criteria().andOperator(Criteria.where("active").is(true),
//				Criteria.where("employeeId").is(resource.getEmployeeId())));
//		EmployeeShift existingEmpShift = mongoTemplate.findOne(getQuery, EmployeeShift.class);
//		if (existingEmpShift != null) {
//			existingEmpShift.setActive(false);
//			existingEmpShift.setAuditFields(loginEmpId, MyTeamUtils.UPDATE);
//			mongoTemplate.save(existingEmpShift);
//			log.info("The shift has been updated::" + existingEmpShift);
//		}
//	}


    public List<EmployeeShift> getAllEmployeeShifts() {
        return empShiftsRepo.findAll();
    }

    public EmployeeShift getEmployeeShift(String employeeId) {
        EmployeeShift empShift = null;
        if (StringUtils.isNotBlank(employeeId)) {
            empShift = empShiftsRepo.findByEmployeeId(employeeId);
        }
        return empShift;
    }


}
