package com.nisum.myteam.service.impl;

import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.dao.EmployeeSubStatus;
import com.nisum.myteam.model.vo.EmployeeSubStatusVO;
import com.nisum.myteam.repository.EmployeeRepo;
import com.nisum.myteam.repository.EmployeeSubStatusRepo;
import com.nisum.myteam.service.ISubStatusService;
import com.nisum.myteam.utils.MyTeamDateUtils;
import com.nisum.myteam.utils.MyTeamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubStatusService implements ISubStatusService {

    @Autowired
    private EmployeeSubStatusRepo employeeSubStatusRepo;
    @Autowired
    private EmployeeRepo employeeRepo;

    public HashMap<String, Object> respMap = new HashMap<>();


    @Override
    public EmployeeSubStatus getLatestEmployeeSubStatus(String empId) {
        List<EmployeeSubStatus> employeeSubStatusList = employeeSubStatusRepo.findByEmployeeID(empId);
        EmployeeSubStatus latestSubStatus = null;
        if(!employeeSubStatusList.isEmpty()){
            latestSubStatus = employeeSubStatusList.get(0);
            for(EmployeeSubStatus s:employeeSubStatusList) {
                if(s.getToDate().after(latestSubStatus.getToDate())){
                    latestSubStatus = s;
                }
            }
        }
        return latestSubStatus;
    }


    @Override
    public EmployeeSubStatus updateExistingEmplyeeSubStatus(String loginnEmployeeId,EmployeeSubStatus employeeSubStatusReq) {
        employeeSubStatusReq.setFromDate(MyTeamDateUtils.getDayMoreThanDate(employeeSubStatusReq.getFromDate()));
        employeeSubStatusReq.setToDate(MyTeamDateUtils.getDayMoreThanDate(employeeSubStatusReq.getToDate()));
        employeeSubStatusReq.setAuditFields(loginnEmployeeId,MyTeamUtils.UPDATE);
        return employeeSubStatusRepo.save(employeeSubStatusReq);
    }

    @Override
    public EmployeeSubStatus addEmployeeSubStatus(String loginnEmployeeId,EmployeeSubStatus employeeSubStatus) {
        employeeSubStatus.setFromDate(MyTeamDateUtils.getDayMoreThanDate(employeeSubStatus.getFromDate()));
        employeeSubStatus.setToDate(MyTeamDateUtils.getDayMoreThanDate(employeeSubStatus.getToDate()));
        employeeSubStatus.setAuditFields(loginnEmployeeId,MyTeamUtils.CREATE);
        return employeeSubStatusRepo.insert(employeeSubStatus);
    }

    @Override
    public EmployeeSubStatus getCurrentSubStatus(String employeeId){
        List<EmployeeSubStatus> currentEmployeeSubStatusList = employeeSubStatusRepo.findByEmployeeID(employeeId).stream().filter(
                s -> s.getFromDate().compareTo(new Date())<=0 && s.getToDate().compareTo(new Date())>=0).collect(Collectors.toList());
        if(!currentEmployeeSubStatusList.isEmpty()){
            return currentEmployeeSubStatusList.get(0);
        }else
            return null;
    }

    @Override
    public EmployeeSubStatus endSubStatus(String loginnEmployeeId,EmployeeSubStatus subStatus){
        EmployeeSubStatus currentSubStatus = getCurrentSubStatus(subStatus.getEmployeeID());
        if(currentSubStatus!=null){
            currentSubStatus.setToDate(MyTeamDateUtils.getDayLessThanDate(new Date()));
            currentSubStatus.setAuditFields(loginnEmployeeId,MyTeamUtils.UPDATE);
            updateExistingEmplyeeSubStatus(loginnEmployeeId,currentSubStatus);
        }
        return null;
    }
 //not called by ANY ONE
    public boolean isDatesValid(EmployeeSubStatus subStatus){
        boolean isValid = false;
        List<EmployeeSubStatus> subStatusList = employeeSubStatusRepo.findByEmployeeID(subStatus.getEmployeeID());
        List<EmployeeSubStatus> matchedList = subStatusList.stream().filter(s -> (s.getFromDate().compareTo(subStatus.getFromDate())<=0 && s.getToDate().compareTo(subStatus.getFromDate())>=0) &&
                (s.getFromDate().compareTo(subStatus.getToDate())<=0 && s.getToDate().compareTo(subStatus.getToDate())>=0)).collect(Collectors.toList());
        if(!matchedList.isEmpty()){
        	respMap.put("statusCode", 811);
            respMap.put("message", "resource is already on substatus on these days");
            //resource is already on substatus on these days
        }else{
            isValid = true;

        }
        return isValid;
    }


	@Override
	public List<EmployeeSubStatusVO> employeesBasedOnSubStatusForGivenDates(Date fromDate, Date toDate, String subStatus) {
		List<EmployeeSubStatusVO> employees=new ArrayList<>();
		List<EmployeeSubStatus> employeswithSubStatus=employeeSubStatusRepo.findBySubStatus(subStatus);
		if(subStatus.equals("All")) {
			employeswithSubStatus=employeeSubStatusRepo.findAll();
		}
			
		employeswithSubStatus.stream().filter(e->isDateCommon(e,fromDate,toDate)	
		 )	
		.forEach(e->{
		Employee employee=employeeRepo.findByEmployeeIdAndEmpStatus(e.getEmployeeID(), MyTeamUtils.ACTIVE);
		if(employee!=null) {
		EmployeeSubStatusVO empSubStatus=new EmployeeSubStatusVO();
		empSubStatus.setEmpId(e.getEmployeeID());
		empSubStatus.setEmpName(employee.getEmployeeName());
		empSubStatus.setEmpStatus(e.getSubStatus());
		empSubStatus.setFromDate(e.getFromDate());
		empSubStatus.setToDate(e.getToDate());
		empSubStatus.setFunctionalGroup(employee.getFunctionalGroup());
		employees.add(empSubStatus);
		}});
		return employees;
	}


	private boolean isDateCommon(EmployeeSubStatus e, Date fromDate, Date toDate) {
		for (LocalDate datei = convert(fromDate); datei.isBefore(convert(toDate).plusDays(1)); datei = datei.plusDays(1))
			for (LocalDate datej = convert(e.getFromDate()); datej.isBefore(convert(e.getToDate()).plusDays(1)); datej = datej.plusDays(1))
			   if(datej.equals(datei))
				   return true;
		return false;
	}


	private LocalDate convert(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime());
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		return localDateTime.toLocalDate();
		
	}

}
