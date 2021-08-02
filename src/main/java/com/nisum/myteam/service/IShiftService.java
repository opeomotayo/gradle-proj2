package com.nisum.myteam.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Shift;

@Service
public interface IShiftService {

	List<Shift> getAllShifts() throws MyTeamException;
}
