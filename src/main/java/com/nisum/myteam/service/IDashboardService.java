package com.nisum.myteam.service;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.vo.EmployeeDashboardVO;

import java.util.List;

public interface IDashboardService {
    public List<EmployeeDashboardVO> getEmployeesDashBoard();
    public List<EmployeeDashboardVO> getDashBoardData() throws MyTeamException;
}
