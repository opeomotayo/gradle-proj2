package com.nisum.myteam.controller;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.exception.handler.ResponseDetails;
import com.nisum.myteam.model.dao.Account;
import com.nisum.myteam.service.IAccountService;
import com.nisum.myteam.statuscodes.AccountStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class AccountController {

    @Autowired
    private IAccountService accountService;

    @RequestMapping(value = "/accounts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAccount(@Valid @RequestBody Account account, HttpServletRequest request)
            throws MyTeamException {

        ResponseDetails respDetails = null;
        boolean isAccountExists = accountService.isAccountExists(account);
        log.info("is Account exists with the name " + account.getAccountName() + ":::" + isAccountExists);
        if (!isAccountExists) {
            Account accountPersisted = accountService.createAccount(account);

            respDetails = new ResponseDetails(new Date(), AccountStatus.CREATE.getCode(), AccountStatus.CREATE.getMessage(),
                    "Account description", null, request.getContextPath(), "details", accountPersisted);

        }else {
        respDetails = new ResponseDetails(new Date(), AccountStatus.ALREADY_EXISTED.getCode(), AccountStatus.ALREADY_EXISTED.getMessage(),
                "Choose the different account name", null, request.getRequestURI(), "details", account);
        }

        return new ResponseEntity<ResponseDetails>(respDetails, HttpStatus.OK);
    }

    @RequestMapping(value = "/accounts/{accountId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAccount(@RequestBody Account account, @PathVariable String accountId,
                                           HttpServletRequest request) throws MyTeamException {
        log.info("Updating the account with details::" + account);
        ResponseDetails respDetails = null;
        boolean isAccountExists = accountService.isAccountExists(account);
        if (isAccountExists == true) {
            Account accountPersisted = accountService.updateAccountAndRolesForDMS(account);

            respDetails = new ResponseDetails(new Date(), AccountStatus.UPDATE.getCode(), AccountStatus.UPDATE.getMessage(),
                    "status description", null, request.getContextPath(), "details", accountPersisted);
        }
        respDetails = new ResponseDetails(new Date(), AccountStatus.NOT_FOUND.getCode(), AccountStatus.NOT_FOUND.getMessage(),
                "Choose the correct updating account name", null, request.getRequestURI(), "details", account);

        return new ResponseEntity<ResponseDetails>(respDetails, HttpStatus.OK);

    }


    @RequestMapping(value = "/accounts/{accountId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteAccount(@PathVariable String accountId, HttpServletRequest request)
            throws MyTeamException {
        log.info("Deleting account with accountId:" + accountId);
        Account accountDeleted = accountService.deleteAccount(accountId);
        ResponseDetails deleteRespDetails = new ResponseDetails(new Date(), AccountStatus.DELETE.getCode(),
                AccountStatus.DELETE.getMessage(), "status description", null, request.getRequestURI(), "details",
                accountDeleted);

        return new ResponseEntity<ResponseDetails>(deleteRespDetails, HttpStatus.OK);

    }

    @RequestMapping(value = "/accounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccounts(HttpServletRequest request) throws MyTeamException {
        List<Map<Object, Object>> accountsList = accountService.getAccountsList();
        log.info("The accounts list::" + accountsList);

        ResponseDetails getRespDetails = new ResponseDetails(new Date(), AccountStatus.GET_ACCOUNTS.getCode(), AccountStatus.GET_ACCOUNTS.getMessage(),
                "Accounts list", accountsList, request.getRequestURI(), "details", null);

        return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

    }
    
//    @RequestMapping(value = "/accountsByLoginId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> getAccounts(@RequestParam("loginId") String loginId ,HttpServletRequest request) throws MyTeamException {
//        List<Map<Object, Object>> accountsList = accountService.getAccountsListByLoginId(loginId);
//        log.info("The accounts list::" + accountsList);
//
//        ResponseDetails getRespDetails = new ResponseDetails(new Date(), AccountStatus.GET_ACCOUNTS.getCode(), AccountStatus.GET_ACCOUNTS.getMessage(),
//                "Accounts list", accountsList, request.getRequestURI(), "details", null);
//
//        return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
//
//    }


    @RequestMapping(value = "/accounts/names", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccountNames(HttpServletRequest request) throws MyTeamException {
        List<Account> acountsList = accountService.getAllAccounts();
        List<String> accountNamesList = new ArrayList<>();

        for (Account account : acountsList) {
            accountNamesList.add(account.getAccountName());
        }
        log.info("The account names list::" + accountNamesList);

        ResponseDetails getRespDetails = new ResponseDetails(new Date(), AccountStatus.GET_ACCOUNT_NAMES.getCode(),
                AccountStatus.GET_ACCOUNT_NAMES.getMessage(), "Account names list", accountNamesList,
                request.getRequestURI(), "details", null);

        return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

    }


    //get the accounts based on status(Active or inactive)
    @RequestMapping(value = "/accounts/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Account>> getAccounts(@RequestParam("status") String status) throws MyTeamException {
        List<Account> accountsList = accountService.getAllAccounts().stream()
                .filter(e -> "Active".equalsIgnoreCase(e.getStatus())).collect(Collectors.toList());
        return new ResponseEntity<>(accountsList, HttpStatus.OK);
    }


    @RequestMapping(value = "/accounts/accountId/{accId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccountName(@PathVariable("accId") String accountId, HttpServletRequest request) throws MyTeamException {

        log.info("Serving the Account Name Get action: accountId:" + accountId);
        ResponseDetails getRespDetails = null;
        if (accountId != null && !"".equalsIgnoreCase(accountId)) {
            boolean isAccountExists = accountService.isAccountExists(accountId);
            if (isAccountExists) {
                getRespDetails = new ResponseDetails(new Date(), AccountStatus.GET_ACCOUNT.getCode(), AccountStatus.GET_ACCOUNT.getMessage(),
                        "Account Name", accountService.getAccountById(accountId), request.getRequestURI(), "details", null);

            }
            getRespDetails = new ResponseDetails(new Date(), AccountStatus.NOT_FOUND.getCode(), AccountStatus.NOT_FOUND.getMessage(),
                    "Account Name", null, request.getRequestURI(), "details", null);

        }
        getRespDetails = new ResponseDetails(new Date(), AccountStatus.NOT_VALID_ID.getCode(), AccountStatus.NOT_VALID_ID.getMessage(),
                "Please provide Valid account Id", null, request.getRequestURI(), "details", null);

        return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

    }

}

