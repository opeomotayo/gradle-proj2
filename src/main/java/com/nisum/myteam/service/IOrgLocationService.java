package com.nisum.myteam.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.OrgLocation;

@Service
public interface IOrgLocationService {

	List<OrgLocation> getLocations() throws MyTeamException;
	
	List<OrgLocation> findByLocationAndActiveStatus(String location, boolean activeStatus);
}
