package com.nisum.myteam.repository;

import com.nisum.myteam.model.dao.EffectiveLoginData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface EffectiveLoginDataRepo extends MongoRepository<EffectiveLoginData, String> {

    List<EffectiveLoginData> findByEmployeeId(String employeeId);

    List<EffectiveLoginData> findByDateBetweenOrderByDate(Date from,Date to);

    void deleteByDate(Date date);
}
