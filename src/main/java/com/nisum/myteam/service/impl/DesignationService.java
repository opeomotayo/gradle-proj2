package com.nisum.myteam.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Designation;
import com.nisum.myteam.repository.DesignationRepo;
import com.nisum.myteam.service.IDesignationService;

@Service
public class DesignationService implements IDesignationService {

	@Autowired
	private DesignationRepo designationRepo;

	@Override
	public List<Designation> getAllDesignations() throws MyTeamException {
		return designationRepo.findAll();
	}
}
