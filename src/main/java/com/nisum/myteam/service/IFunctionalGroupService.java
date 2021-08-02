package com.nisum.myteam.service;

import com.nisum.myteam.model.FunctionalGroup;
import java.util.List;

public interface IFunctionalGroupService {


    public List<FunctionalGroup> getAllFunctionalGroups();

    public FunctionalGroup getFunctionalGroup(String functionalGroupName);

    public List<FunctionalGroup> getAllBillableFunctionalGroups();
}
