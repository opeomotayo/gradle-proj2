package com.nisum.myteam.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.myteam.model.dao.MasterData;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MasterDataRepo extends MongoRepository<MasterData, String> {

	List<MasterData> findByMasterDataTypeAndMasterDataNameAndActiveStatus(String masterDataType, String masterDataName, boolean activeStatus);

	List<MasterData> findByMasterDataType(String masterDataType);

	MasterData findByMasterDataTypeAndMasterDataIdAndActiveStatus(String masterDataType, String masterDataId, boolean activeStatus);

	void deleteByMasterDataTypeAndMasterDataId(String masterDataType, String masterDataId);

	boolean existsByMasterDataTypeAndMasterDataId(String masterDataType,String masterDataId);

	MasterData findByMasterDataTypeAndMasterDataId(String masterDataType,String masterDataId);

	boolean existsByMasterDataTypeAndMasterDataNameIgnoreCase(String masterDataType,String masterDataName);
}