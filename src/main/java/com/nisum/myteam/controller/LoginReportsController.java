package com.nisum.myteam.controller;

import com.nisum.myteam.service.ILoginReportsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LoginReportsController {


    @Autowired
    private ILoginReportsService reportsService;

    @RequestMapping(value = "/deleteReport/{fileName}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deletePdfReport(@PathVariable("fileName") String fileName) {
        String response = reportsService.deletePdfReport(fileName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
