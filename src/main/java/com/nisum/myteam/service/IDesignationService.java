package com.nisum.myteam.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Designation;

@Service
public interface IDesignationService {

	List<Designation> getAllDesignations() throws MyTeamException;
}
