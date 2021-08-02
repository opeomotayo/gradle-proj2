package com.nisum.myteam.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Shift;
import com.nisum.myteam.repository.ShiftRepo;
import com.nisum.myteam.service.IShiftService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShiftService implements IShiftService {

	@Autowired
	private ShiftRepo shiftRepo;

	@Override
	public List<Shift> getAllShifts() throws MyTeamException {
		List<Shift> shiftsList = shiftRepo.findAll();
		log.info("The shift list details::" + shiftsList);
		return shiftsList;
	}
	
	
	
	
}
