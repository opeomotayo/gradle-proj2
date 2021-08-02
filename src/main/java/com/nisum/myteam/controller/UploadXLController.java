package com.nisum.myteam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.service.IUploadXLService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UploadXLController {

	@Autowired
	private IUploadXLService uploadService;

	@RequestMapping(value = "/employee/fileUpload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> exportDataFromFile(@RequestParam(value = "file") MultipartFile file,
			@RequestParam(value = "empId") String loginEmpId) throws MyTeamException {
		log.info("Uploaded file: {} with size: {} By employee: {}", file.getOriginalFilename(), file.getSize(), loginEmpId);
		String result = uploadService.importDataFromExcelFile(file, loginEmpId);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
