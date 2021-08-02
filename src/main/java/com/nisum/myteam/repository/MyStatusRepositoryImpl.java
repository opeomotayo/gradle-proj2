package com.nisum.myteam.repository;

import com.nisum.myteam.model.dao.MyStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public class MyStatusRepositoryImpl {

  private final MongoTemplate mongoTemplate;

  public MyStatusRepositoryImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public List<MyStatus> findByempId(String empId){
    try{
      Query query = new Query().addCriteria(Criteria.where("empId").is(empId));
      return mongoTemplate.find(query, MyStatus.class);
    }
    catch(Exception e){
      throw e;
    }
  }

  public List<MyStatus> findByDateRange(Date fromDate, Date toDate, String empId){
    try{
      Query query = new Query().addCriteria(Criteria.where("empId").is(empId)
        .andOperator(
          Criteria.where("taskDate").gte(fromDate),
          Criteria.where("taskDate").lte(toDate))
      );
      //query.with(Sort.by(Sort.Order.desc("taskDate")));
      return mongoTemplate.find(query, MyStatus.class);
    }
    catch(Exception e){
      throw e;
    }
  }

  //@Query(value = "SELECT *FROM mystatus_db.mystatus t WHERE t.emp_id =?1 and (select count(distinct t2.task_date) from mystatus_db.mystatus t2 where t2.emp_id = t.emp_id and t2.task_date >= t.task_date) <= 5 order by t.task_date desc")
  public List<MyStatus> findByLastFiveDays(String empId){
    try{
      Query query = new Query().addCriteria(Criteria.where("empId").is(empId)
      );
      query.limit(20);
      //query.with(Sort.by(Sort.Order.desc("taskDate"))).limit(20);
      return mongoTemplate.find(query, MyStatus.class);
    }
    catch(Exception e){
      throw e;
    }
  }

  public List<MyStatus> findTodayStatus(String empId, LocalDate today){
    try{
      Query query = new Query().addCriteria(Criteria.where("empId").is(empId).and("taskDate").is(today));
      return mongoTemplate.find(query, MyStatus.class);
    }
    catch(Exception e){
      throw e;
    }

  }

}
