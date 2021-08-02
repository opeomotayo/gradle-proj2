package com.nisum.myteam.service.impl;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.MasterData;
import com.nisum.myteam.model.vo.DesignationCountVO;
import com.nisum.myteam.repository.MasterDataRepo;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IMasterDataService;
import com.nisum.myteam.utils.MyTeamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("masterDataService")
public class MasterDataService implements IMasterDataService {

    @Autowired
    private MasterDataRepo masterDataRepo;

    @Autowired
    IEmployeeService employeeService;

    @Override
    public List<MasterData> getMasterData() throws MyTeamException {
        return masterDataRepo.findAll();
    }

    @Override
    public List<MasterData> findByMasterDataTypeAndMasterDataNameAndActiveStatus(String masterDataType, String masterDataName,
                                                                                 boolean activeStatus) {
        return masterDataRepo.findByMasterDataTypeAndMasterDataNameAndActiveStatus(masterDataType, masterDataName,
                activeStatus);
    }

    @Override
    public List<DesignationCountVO> getDesignationsWithEmployeeCount() {
        return masterDataRepo.findByMasterDataType(MyTeamUtils.TEXT_DESIGNATIONS).stream()
                .map(designation -> new DesignationCountVO(designation.getMasterDataId(), designation.getMasterDataName(), employeeService.getEmployeesCountByDesignation(designation.getMasterDataName()), designation.isActiveStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public DesignationCountVO addDesignation(String designationName) throws MyTeamException {
        if (masterDataRepo.existsByMasterDataTypeAndMasterDataNameIgnoreCase(MyTeamUtils.TEXT_DESIGNATIONS, designationName)) {
            List<MasterData> masterData = masterDataRepo.findByMasterDataTypeAndMasterDataNameAndActiveStatus(MyTeamUtils.TEXT_DESIGNATIONS, designationName, true);
            if (masterData.size() > 0)
                throw new MyTeamException("Designation '" + designationName + "' already exists");
            else throw new MyTeamException("Designation '" + designationName + "' already exists and is inactive");
        }
        return masterDataRepo.findByMasterDataType(MyTeamUtils.TEXT_DESIGNATIONS).stream()
                .map(MasterData::getMasterDataId)
                .max(Comparator.comparing(Integer::parseInt))
                .map(desId -> Integer.parseInt(desId) + 1)
                .map(maxId -> masterDataRepo.save(new MasterData(MyTeamUtils.TEXT_DESIGNATIONS, maxId.toString(), designationName, true)))
                .map(masterData -> new DesignationCountVO(masterData.getMasterDataId(), masterData.getMasterDataName(), 0, true))
                .get();
    }

    @Override
    public void deleteDesignation(String designationId) throws MyTeamException {
        if (masterDataRepo.existsByMasterDataTypeAndMasterDataId(MyTeamUtils.TEXT_DESIGNATIONS, designationId)) {
            MasterData masterData = Optional.ofNullable(masterDataRepo.findByMasterDataTypeAndMasterDataIdAndActiveStatus(MyTeamUtils.TEXT_DESIGNATIONS, designationId, false))
                    .orElseThrow(() -> new MyTeamException("Disable designation before deleting"));
            masterDataRepo.delete(masterData);
        } else
            throw new MyTeamException("Designation doesn't exist");
    }

    @Override
    public void changeDesignationStatus(String designationId, String action) throws MyTeamException {
        if (masterDataRepo.existsByMasterDataTypeAndMasterDataId(MyTeamUtils.TEXT_DESIGNATIONS, designationId)) {
            MasterData masterData = masterDataRepo.findByMasterDataTypeAndMasterDataId(MyTeamUtils.TEXT_DESIGNATIONS, designationId);
            if (action.equals(MyTeamUtils.TEXT_ENABLE) && !masterData.isActiveStatus()) {
                masterData.setActiveStatus(true);
                masterDataRepo.save(masterData);
            } else if (action.equals(MyTeamUtils.TEXT_DISABLE) && masterData.isActiveStatus()) {
                masterData.setActiveStatus(false);
                masterDataRepo.save(masterData);
            } else {
                String msg = action.equals(MyTeamUtils.TEXT_ENABLE) ? "enabled" : "disabled";
                throw new MyTeamException("Designation is already " + msg);
            }
        } else throw new MyTeamException("Designation doesn't exist");
    }
}
