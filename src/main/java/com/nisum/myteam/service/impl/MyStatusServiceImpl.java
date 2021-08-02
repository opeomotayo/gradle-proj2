package com.nisum.myteam.service.impl;

import com.itextpdf.styledxmlparser.jsoup.helper.StringUtil;
import com.nisum.myteam.exception.handler.EmployeeNotFoundException;
import com.nisum.myteam.service.MyStatusService;
import com.nisum.myteam.service.SequenceGeneratorService;
import com.nisum.myteam.model.dao.MyStatus;
import com.nisum.myteam.repository.MyStatusRepository;
import com.nisum.myteam.repository.MyStatusRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MyStatusServiceImpl implements MyStatusService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyStatusServiceImpl.class);

  @Autowired
  private MyStatusRepository repository;

  @Autowired
  private SequenceGeneratorService sequenceGeneratorService;

  @Autowired
  private MyStatusRepositoryImpl statusRepositoryImpl;

  /**
   * Save the task details
   * @param status
   * @return
   */
  public MyStatus saveStatusDetails(MyStatus status) {
    Optional<MyStatus> s = Optional.ofNullable(status);
    if(s.isPresent()){
      status.setId((int) sequenceGeneratorService.generateSequence(MyStatus.SEQUENCE_NAME));
      if(StringUtil.isBlank(status.getTicketNumber())){
        status.setTicketNumber("NA");
      }
      if(StringUtil.isBlank(status.getHoursSpent())){
        status.setHoursSpent("NA");
      }
      if(StringUtil.isBlank(status.getTaskDetails())){
        status.setTaskDetails("NA");
      }
      if(StringUtil.isBlank(status.getPriority())){
        status.setPriority("NA");
      }
      if(StringUtil.isBlank(status.getTaskType())){
        status.setTaskType("NA");
      }
      if(StringUtil.isBlank(status.getStatus())){
        status.setStatus("NA");
      }
    }
    MyStatus saveStatus = s.get();
    LOGGER.info("Request for saving task details : "+saveStatus);
    return repository.save(saveStatus);
  }

  /**
   * Get the today task details based on employee ID and today date
   * @param empId
   * @return
   */
  public List<MyStatus> findTodayStatus(String empId){
    LocalDate today = LocalDate.now();
    List<MyStatus> status = null;
    try{
      if(empId != null){
        status = statusRepositoryImpl.findTodayStatus(empId, today);
      }
    }
    catch(Exception e){
      throw e;
    }
    return status;
  }

  /**
   * Find task details by task date and employee id
   * @param empId
   * @param date
   * @return status
   */
  public List<MyStatus> findTaskDataByTaskDate(String empId, LocalDate date){
    List<MyStatus> status = null;
    try{
      if(!StringUtil.isBlank(empId) && date != null){
        status = statusRepositoryImpl.findTodayStatus(empId, date);
        //Query query = new Query().addCriteria(Criteria.where("empId").is(empId).and("taskDate").is(date));
        //status =  mongoTemplate.find(query, Status.class);
      }
    }
    catch(Exception e){
      throw e;
    }
    return status;
  }

  /**
   * Fetch the task details based on employee ID
   * @param empId
   * @return
   */
  public List<MyStatus> getStatusDetailsByEmpId(String empId){
    try{
      return statusRepositoryImpl.findByempId(empId);
    }catch(Exception e){
      throw e;
    }
  }

  /**
   * Fetch the task details based on date range(fromDate to toDate)
   * @param fromDate
   * @param toDate
   * @param empId
   * @return
   */
  public List<MyStatus> getByDateRange(Date fromDate, Date toDate, String empId){
    try{
      return statusRepositoryImpl.findByDateRange(fromDate, toDate, empId);
    }catch(Exception e){
      throw e;
    }
  }

  /**
   * Find the all employees task details
   * @return
   */
  public List<MyStatus> getEmployeeStatusDetails() {
    try{
      return repository.findAll();
    }
    catch(Exception e){
      throw e;
    }
  }

  /**
   * Update the task details based on task ID
   * @param status
   * @return
   */
  public MyStatus updateStatusDetails(MyStatus status) {
    Optional<MyStatus> optionalStatus = repository.findById(status.getId());
    if (optionalStatus.isPresent()) {
      MyStatus updatedStatus = optionalStatus.get();
      updatedStatus.setTaskType(status.getTaskType());
      updatedStatus.setTaskDate(status.getTaskDate());
      updatedStatus.setStoryPoints(status.getStoryPoints());
      updatedStatus.setHoursSpent(status.getHoursSpent());
      updatedStatus.setTicketNumber(status.getTicketNumber());
      updatedStatus.setPriority(status.getPriority());
      updatedStatus.setPlanedStartDate(status.getPlanedStartDate());
      updatedStatus.setPlanedEndDate(status.getPlanedEndDate());
      updatedStatus.setActualStartDate(status.getActualStartDate());
      updatedStatus.setActualEndDate(status.getActualEndDate());
      updatedStatus.setComments(status.getComments());
      updatedStatus.setStatus(status.getStatus());
      updatedStatus.setTaskDetails(status.getTaskDetails());
      updatedStatus.setTaskAddedTime(LocalDateTime.now());
      repository.save(updatedStatus);
      return updatedStatus;
    } else {
      throw new EmployeeNotFoundException("Employee not found on "+status.getId() + " Id");
    }
  }

  /**
   * Delete the task details based on task ID
   * @param id
   */
  public void deleteEmployeeStatusDetails(Integer id){
    try{
      if(repository.existsById(id)){
        repository.deleteById(id);
      }
    }catch(Exception e){
      throw e;
    }
  }

  /**
   * fetch the last five dates records based on employee ID
   * @param empId
   * @return
   */
  public List<MyStatus> getDefaultStatusDetails(String empId) {
    try{
      List<MyStatus> status = statusRepositoryImpl.findByLastFiveDays(empId);
      LOGGER.info("Response for last five dates task details : "+status);
      return status;
    }catch(Exception e){
      throw e;
    }
  }

}
