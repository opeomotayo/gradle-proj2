package com.nisum.myteam.service;

import java.util.List;

import com.nisum.myteam.model.dao.Designation;
import com.nisum.myteam.model.vo.DesignationCountVO;
import org.springframework.stereotype.Service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.MasterData;

@Service
public interface IMasterDataService  {

	 List<MasterData> getMasterData() throws MyTeamException;
	 
	 List<MasterData> findByMasterDataTypeAndMasterDataNameAndActiveStatus(String masterDataType, String masterDataName,
				boolean activeStatus);

	 List<DesignationCountVO> getDesignationsWithEmployeeCount();

	DesignationCountVO addDesignation(String designationName) throws MyTeamException, Exception;

	void deleteDesignation(String designationId) throws MyTeamException;

	void changeDesignationStatus(String designationId,String action) throws MyTeamException;

}
