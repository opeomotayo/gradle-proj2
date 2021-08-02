package com.nisum.myteam.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Skill;
import com.nisum.myteam.repository.SkillRepo;
import com.nisum.myteam.service.ISkillService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SkillService implements ISkillService {

	@Autowired
	private SkillRepo technologyRepo;

	@Override
	public List<Skill> getTechnologies() throws MyTeamException {
		return technologyRepo.findAll();
	}
}
