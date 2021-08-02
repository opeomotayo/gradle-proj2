package com.nisum.myteam.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.OrgLocation;
import com.nisum.myteam.repository.OrgLocationRepo;
import com.nisum.myteam.service.IOrgLocationService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrgLocationService implements IOrgLocationService {

	@Autowired
	private OrgLocationRepo orgLocationRepo;

	@Override
	public List<OrgLocation> getLocations() throws MyTeamException {
		List<OrgLocation> orgLocationList = orgLocationRepo.findAll();
		log.info("The Organization Location Details::" + orgLocationList);
		return orgLocationList;

	}

	@Override
	public List<OrgLocation> findByLocationAndActiveStatus(String location, boolean activeStatus) {
		return orgLocationRepo.findByLocationAndActiveStatus(location, activeStatus);
	}

}
