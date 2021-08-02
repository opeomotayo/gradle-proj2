package com.nisum.myteam.controller;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.exception.handler.ResponseDetails;
import com.nisum.myteam.model.dao.Domain;
import com.nisum.myteam.service.IDomainService;
import com.nisum.myteam.statuscodes.DomainStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vijay
 */
@RestController
@Slf4j
public class DomainController {

    @Autowired
    private IDomainService domainService;

    @RequestMapping(value = "/domains", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createDomain(@RequestBody Domain domain, HttpServletRequest request)
            throws MyTeamException {

        log.info("Domain Creation");

        if (!domainService.isDomainExists(domain)) {
            Domain domainPeristed = domainService.create(domain);

            ResponseDetails createRespDetails = new ResponseDetails(new Date(), DomainStatus.CREATE.getCode(), DomainStatus.CREATE.getMessage(),
                    "Domain Creation", null, "", "details", domainPeristed);
            return new ResponseEntity<ResponseDetails>(createRespDetails, HttpStatus.OK);
        }
        log.info("A domain is already existed with the requested name" + domain.getDomainName());

        ResponseDetails responseDetails = new ResponseDetails(new Date(), DomainStatus.ALREADY_EXISTED.getCode(), DomainStatus.ALREADY_EXISTED.getMessage(),
                "Choose the different domain name", null, request.getRequestURI(), "Domain details", domain);

        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);

    }


    @RequestMapping(value = "/domains", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDomain(@RequestBody Domain domain, HttpServletRequest request)
            throws MyTeamException {

        boolean isDomainExists = domainService.isDomainExists(domain);
        if (isDomainExists == true) {
            Domain domainPersisted = domainService.update(domain);
            ResponseDetails updateRespDetails = new ResponseDetails(new Date(), DomainStatus.UPDATE.getCode(), DomainStatus.UPDATE.getMessage(),
                    "Domain Updation", null, request.getRequestURI(), "Updation Domain details", domainPersisted);

            return new ResponseEntity<ResponseDetails>(updateRespDetails, HttpStatus.OK);
        }
        ResponseDetails responseDetails = new ResponseDetails(new Date(), 803, "Domain is Not found",
                "Choose the correct updating domain name", null, request.getRequestURI(), "details", domain);
        return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);

    }

    @RequestMapping(value = "/domains/{domainId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteDomain(@PathVariable String domainId, HttpServletRequest request)
            throws MyTeamException {
        domainService.delete(domainId);

        ResponseDetails deleteRespDetails = new ResponseDetails(new Date(), 804, "Domain has been deleted",
                "Domain Deletion", null, request.getRequestURI(), "Deletion Domain details", domainId);

        return new ResponseEntity<ResponseDetails>(deleteRespDetails, HttpStatus.OK);

    }


    @RequestMapping(value = "/domains", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDomains(HttpServletRequest request) throws MyTeamException {

        ResponseDetails getRespDetails = new ResponseDetails(new Date(), DomainStatus.GET_DOMIAINS.getCode(), DomainStatus.GET_DOMIAINS.getMessage(),
                "Domains list", domainService.getDomainsList(), request.getRequestURI(), "details", null);

        return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
    }
    
//    @RequestMapping(value = "/domainsByLoginId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> getDomains(@RequestParam("loginId") String loginId ,HttpServletRequest request) throws MyTeamException {
//
//        ResponseDetails getRespDetails = new ResponseDetails(new Date(), DomainStatus.GET_DOMIAINS.getCode(), DomainStatus.GET_DOMIAINS.getMessage(),
//                "Domains list", domainService.getDomainsListByLoginId(loginId), request.getRequestURI(), "details", null);
//
//        return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
//    }

    //getting domains list under accountId which is an active.
    @RequestMapping(value = "/domains/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Domain>> getDomains(@PathVariable("accountId") String accountId) throws MyTeamException {
        List<Domain> domains = domainService.getDomainsUnderAccount(accountId).stream()
                .filter(e -> "Active".equalsIgnoreCase(e.getStatus())).collect(Collectors.toList());
        return new ResponseEntity<>(domains, HttpStatus.OK);
    }


}