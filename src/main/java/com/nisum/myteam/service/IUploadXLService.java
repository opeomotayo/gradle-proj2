package com.nisum.myteam.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nisum.myteam.exception.handler.MyTeamException;

@Service
public interface IUploadXLService {

	String importDataFromExcelFile(MultipartFile file, String empId) throws MyTeamException;
}
