package com.nisum.myteam.controller;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.exception.handler.ResponseDetails;
import com.nisum.myteam.model.dao.Designation;
import com.nisum.myteam.service.IDesignationService;
import com.nisum.myteam.service.IMasterDataService;
import com.nisum.myteam.utils.MyTeamUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
public class DesignationController {

    @Autowired
    IDesignationService designationService;

    @Autowired
    IMasterDataService masterDataService;

    // @RequestMapping(value = "/getAllDesignations"
    @RequestMapping(value = "/employees/designations/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllDesignations(HttpServletRequest request) throws MyTeamException {
        List<String> designations = designationService.getAllDesignations().stream()
                .filter(e -> "Y".equalsIgnoreCase(e.getActiveStatus())).map(Designation::getDesignationName).sorted()
                .collect(Collectors.toList());

        ResponseDetails getRespDetails = new ResponseDetails(new Date(), 908, "Retrieved Designations successfully",
                "Designations list", designations, request.getRequestURI(), "Employee Designation Details", null);
        return new ResponseEntity<>(getRespDetails, HttpStatus.OK);
    }

    @GetMapping("/designations")
    public ResponseEntity<?> getDesignationsWithEmployeeCount(HttpServletRequest request) {
        ResponseDetails response = new ResponseDetails(new Date(), 200, "Retrieved designations with employee count",
                "Designations with employee count with active status", masterDataService.getDesignationsWithEmployeeCount(), request.getRequestURI(), "Designation Details with Employee count", null);
        return new ResponseEntity<ResponseDetails>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/designations", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> addDesignation(@RequestBody @NotBlank String designationName, HttpServletRequest request) throws Exception {
        ResponseDetails response = new ResponseDetails(new Date(), 201, "Added designation successfully",
                "created Designation '" + designationName + "'", masterDataService.addDesignation(designationName.trim()), request.getRequestURI(), "Added designation", null);
        return new ResponseEntity<ResponseDetails>(response, HttpStatus.OK);
    }

    @PatchMapping(value = "/designations/status/{designationId}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> changeDesignationStatus(@PathVariable("designationId") @NotBlank String designationId, @RequestBody @NotBlank String action, HttpServletRequest request) throws MyTeamException {
        action = action.trim().toLowerCase();
        ResponseDetails response = null;
        if (action.equals(MyTeamUtils.TEXT_ENABLE) || action.equals(MyTeamUtils.TEXT_DISABLE)) {
            masterDataService.changeDesignationStatus(designationId, action);
            String msg = action.equals(MyTeamUtils.TEXT_ENABLE) ? "enabled" : "disabled";
            response = new ResponseDetails(new Date(), 200, "Changed Designation status",
                    "Changed Designation status", null, request.getRequestURI(), "Changed designation status", null);
        } else throw new MyTeamException("Invalid action");
        return new ResponseEntity<ResponseDetails>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/designations/{designationId}")
    public ResponseEntity<?> deleteDesignation(@PathVariable @NotBlank String designationId, HttpServletRequest request) throws MyTeamException {
        masterDataService.deleteDesignation(designationId.trim());
        ResponseDetails response = new ResponseDetails(new Date(), 200, "Deleted designation",
                "Deleted designation successfully", null, request.getRequestURI(), "Deleted designation", null);
        return new ResponseEntity<ResponseDetails>(response, HttpStatus.OK);
    }

}
