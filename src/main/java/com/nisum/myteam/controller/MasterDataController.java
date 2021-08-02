package com.nisum.myteam.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.MasterData;
import com.nisum.myteam.service.IMasterDataService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MasterDataController {

	@Autowired
	IMasterDataService masterDataService;

	@RequestMapping(value = "/getMasterData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, List<String>>> getMasterData() throws MyTeamException {
		Map<String, List<String>> masterDataMap = new HashMap<>();
		Map<String, List<MasterData>> result = masterDataService.getMasterData().stream()
				.filter(e -> (e.isActiveStatus() == true))
				.collect(Collectors.groupingBy(MasterData::getMasterDataType));
		for (String key : result.keySet()) {
			List<MasterData> valueList = result.get(key);
			List<String> technologies = valueList.stream().map(MasterData::getMasterDataName).sorted()
					.collect(Collectors.toList());
			masterDataMap.put(key, technologies);
		}
		return new ResponseEntity<>(masterDataMap, HttpStatus.OK);

	}
}
