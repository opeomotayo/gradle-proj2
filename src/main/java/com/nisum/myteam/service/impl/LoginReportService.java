package com.nisum.myteam.service.impl;


import com.nisum.myteam.service.ILoginReportsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class LoginReportService implements ILoginReportsService {

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public String deletePdfReport(String fileName) {
        String response = "";
        try {
            File file = resourceLoader.getResource("/WEB-INF/reports/" + fileName + ".pdf").getFile();
            if (null != file && file.exists()) {
                boolean status = file.delete();
                if (status) {
                    response = "Success";
                }
            }
        } catch (IOException e) {
            response = "Report deletion failed due to: " + e.getMessage();
            log.error("Report deletion failed due to: ", e);
        }
        return response;
    }
}
