package com.nisum.myteam.repository;


import com.nisum.myteam.model.dao.MyStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

//@Repository
public interface MyStatusRepository extends MongoRepository<MyStatus, Integer> {

    public Optional<MyStatus> findById(Integer id);
    public Boolean existsById(Integer id);
    public void deleteById(Integer id);

}
