package com.nisum.myteam.exception.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import javax.security.auth.login.AccountNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RestController
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	// private static final Logger log =
	// LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(DataAccessException.class)
	public String handleDataAccessExceptions(DataAccessException ex) {
		log.error("DataAccessException occured due to: ", ex);
		return "DataAccessException occured due to: " + ex.toString();

	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		log.error("Exception occurred due to: ", ex);
		ResponseDetails errorDetails = new ResponseDetails(new Date(), 502, "Internal Server error occured",
				ex.getMessage(), null, request.getContextPath(), request.getDescription(false), null);
		return new ResponseEntity<Object>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex,WebRequest request) {
		String exceptionMessage=new ArrayList<>(ex.getConstraintViolations()).get(0).getMessage();
		String invalidField=new ArrayList<>(ex.getConstraintViolations()).get(0).getPropertyPath().toString();
		ResponseDetails errorDetails = new ResponseDetails(new Date(), 400, exceptionMessage,
				invalidField+" : "+exceptionMessage, null, request.getContextPath(), request.getDescription(false), null);
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AccountNotFoundException.class)
	public final ResponseEntity<Object> handleUserNotFoundException(AccountNotFoundException ex, WebRequest request) {
		log.error("Exception occurred due to::", ex);
		ResponseDetails errorDetails = new ResponseDetails(new Date(), 602,
				"The Account you are looking for is not found", ex.getMessage(), null, request.getContextPath(),
				request.getDescription(false), null);
		return new ResponseEntity<Object>(errorDetails, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(EmployeeNotFoundException.class)
	public final ResponseEntity<Object> handleEmployeeNotFoundException(EmployeeNotFoundException ex, WebRequest request) {
		log.error("Exception occured due to::", ex);
		ResponseDetails errorDetails = new ResponseDetails(new Date(), 902,
				"The Employee you are looking for is not found", ex.getMessage(), null, request.getContextPath(),
				request.getDescription(false), null);
		return new ResponseEntity<Object>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error("The exception is occured due to ::", ex);
		ResponseDetails errorDetails = new ResponseDetails(new Date(), 702, "Method Argument is not validated",
				"Validation Failed", null, request.getContextPath(), ex.getBindingResult().toString(), null);
		return new ResponseEntity<Object>(errorDetails, HttpStatus.BAD_REQUEST);
	}
}