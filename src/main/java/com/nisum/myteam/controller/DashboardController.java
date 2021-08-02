package com.nisum.myteam.controller;


import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.exception.handler.ResponseDetails;
import com.nisum.myteam.model.vo.EmployeeDashboardVO;
import com.nisum.myteam.service.IDashboardService;
import com.nisum.myteam.service.impl.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping
public class DashboardController {

    @Autowired
    private IDashboardService dashboardService;

    @RequestMapping(value = "/resources/getEmployeesDashBoard", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getEmployeesDashBoard(HttpServletRequest request) throws MyTeamException {
        List<EmployeeDashboardVO> employeeDashBoardList = dashboardService.getDashBoardData();

        ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Resources have been retrieved successfully",
                "List of Resources for dashboard", employeeDashBoardList, request.getRequestURI(), "Resource details", null);
        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);
    }


}
