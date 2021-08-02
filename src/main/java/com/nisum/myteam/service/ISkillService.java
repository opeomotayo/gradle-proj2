package com.nisum.myteam.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Skill;

@Service
public interface ISkillService {
	List<Skill> getTechnologies() throws MyTeamException;
}
