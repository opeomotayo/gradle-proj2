package com.nisum.myteam.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nisum.myteam.model.dao.EmployeeSubStatus;
import com.nisum.myteam.model.dao.EmployeeVisa;
import com.nisum.myteam.model.vo.EmployeeSubStatusVO;
import com.nisum.myteam.repository.EmployeeVisaRepo;
import com.nisum.myteam.service.ISubStatusService;
import com.nisum.myteam.service.impl.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.exception.handler.ResponseDetails;
import com.nisum.myteam.model.dao.Account;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.service.IEmployeeRoleService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class EmployeeController {

	@Autowired
	private EmployeeService empService;

	@Autowired
	private IEmployeeRoleService employeeRoleService;

	@Autowired
	private EmployeeVisaRepo employeeVisaRepo;

	@Autowired
	private ISubStatusService subStatusService;


	@RequestMapping(value = "/employees/{empId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createEmployee( @RequestBody Employee employeeReq,
											 @PathVariable(value = "empId") String loginEmpId,
											 HttpServletRequest request) throws MyTeamException {

		if (empService.isEmployeeExistsById(loginEmpId)) {
			Employee employeePersisted = empService.createEmployee(employeeReq, loginEmpId);
			
			ResponseDetails createRespDetails = new ResponseDetails(new Date(), 901, "Employee has been created",
					"Employee Creation",null, request.getContextPath(), "Employee Creation Details", employeePersisted);
			return new ResponseEntity<ResponseDetails>(createRespDetails, HttpStatus.OK);
			
		}
		ResponseDetails createRespDetails = new ResponseDetails(new Date(), 907,
				"An Employee is already existed by the Id", "Choose the different employee Id", null,
				request.getRequestURI(), "Employee details", loginEmpId);

		return new ResponseEntity<ResponseDetails>(createRespDetails, HttpStatus.OK);

	}

	@RequestMapping(value = "/employees/{empId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateEmployee(@RequestBody Employee employeeReq,
											@PathVariable(value = "empId") String loginEmpId,
											HttpServletRequest request) throws MyTeamException, ParseException {
		if (empService.isEmployeeExistsById(loginEmpId)) {
			Employee employeeUpdated = empService.updateEmployee(employeeReq, loginEmpId);

			ResponseDetails updateRespDetails = new ResponseDetails(new Date(), 906, empService.response.get("messege").toString() ,
					"Employee Updation", null, request.getRequestURI(), "Updation Employee details",
					employeeUpdated);
			return new ResponseEntity<ResponseDetails>(updateRespDetails, HttpStatus.OK);
		}

		ResponseDetails updateRespDetails = new ResponseDetails(new Date(), 907, "Employee is Not found",
				"Choose the correct Employee Id", null, request.getRequestURI(), "Employee Updation details",
				loginEmpId);
		return new ResponseEntity<ResponseDetails>(updateRespDetails, HttpStatus.NOT_FOUND);

	}

	@RequestMapping(value = "/employees/{empId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteEmployee(@PathVariable("empId") String empId,
											HttpServletRequest request) {

		if (empService.isEmployeeExistsById(empId)) {

			empService.deleteEmployee(empId);

			ResponseDetails deleteRespDetails = new ResponseDetails(new Date(), 908, "Employee status is deActivated",
					"Employee Deletion", null, request.getRequestURI(), "Employee Deletion details", empId);
			return new ResponseEntity<ResponseDetails>(deleteRespDetails, HttpStatus.OK);
		}
		ResponseDetails deleteRespDetails = new ResponseDetails(new Date(), 907, "Employee is Not found",
				"Choose correct Employee Id", null, request.getRequestURI(), "Employee Updation details", empId);
		return new ResponseEntity<ResponseDetails>(deleteRespDetails, HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/employees/updateProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Employee> updateProfile(@RequestBody Employee employee) throws MyTeamException {
		Employee employeeUpdated = empService.updateProfile(employee);
		return new ResponseEntity<>(employeeUpdated, HttpStatus.OK);
	}

	
	@RequestMapping(value = "/employees/employeeId/{empId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEmployeeById(@PathVariable("empId") String empId,
											 HttpServletRequest request)
			throws MyTeamException {
		Employee employee = empService.getEmployeeById(empId);
		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 904, "Retrieved Employee successfully",
				"Employee", employee, request.getRequestURI(), "Employee Details", null);

		return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
	}

	///employees/emailId/{emailId:.+}
	@RequestMapping(value = "/employees/emailId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEmployee(@RequestParam("emailId") String emailId, HttpServletRequest request)
			throws MyTeamException {
		Employee employee = empService.getEmployeeByEmaillId(emailId); 
		if (employee == null) {
			ResponseDetails errorDetails = new ResponseDetails(new Date(), 902,
					"The Employee you are looking for is not found", "Employee List", emailId, request.getContextPath(),
					"Employee Details", null);
			return new ResponseEntity<Object>(errorDetails, HttpStatus.NOT_FOUND);
		} else {
			if (employee.getRole() != null && employee.getRole().equalsIgnoreCase("Admin")) {
				employee.setRole("Admin");
			} else {
				String roleName = employeeRoleService.getEmployeeRole(employee.getEmployeeId());
				if (roleName != null) {
					employee.setRole(roleName);
				}
			}

		}

		log.info("emailId" + emailId + "result" + employee);

		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 904, "Retrieved Employee successfully",
				"Employee list", employee, request.getRequestURI(), "Employee Details", null);

		return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
	}

	@RequestMapping(value = "/employees/managers/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getManagers(HttpServletRequest request) throws MyTeamException {
		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905, "Retrieved Managers successfully",
				"Managers list", empService.getManagers(), request.getRequestURI(), "Managers Details", null);
		return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

	}

	@RequestMapping(value = "/employees/active", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getActiveEmployees(HttpServletRequest request) throws MyTeamException {

		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905, "Retrieved Active Employees successfully",
				"Active Employees list", empService.getActiveEmployees(), request.getRequestURI(),
				"Active Employees Details", null);
		return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

	}

	@RequestMapping(value = "/employees/accounts/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAccounts(HttpServletRequest request) throws MyTeamException {
		List<Account> activeAccountList = empService.getAccounts().stream()
				.filter(e -> "Y".equalsIgnoreCase(e.getStatus()))
				// .filter(a -> !("Nisum
				// India".equalsIgnoreCase(a.getAccountName())))
				// .map(Account::getAccountName).sorted()
				.collect(Collectors.toList());

		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905, "Retrieved Accounts  successfully",
				"Accounts list", activeAccountList, request.getRequestURI(), "Account Details", null);
		return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

	}

	@RequestMapping(value = "/employees/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEmployeeByStatus(@RequestParam("status") String status, HttpServletRequest request) {

		List<Employee> employeeList = empService.getEmployeesByStatus(status);

		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905, "Retrieved Employees  successfully",
				"Employees list by status", employeeList, request.getRequestURI(), "Employee Details: Status", null);
		return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
		

	}
	
	@RequestMapping(value = "/employees/getAllEmployees", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAllEmployees(HttpServletRequest request) {

		List<Employee> employeeList = empService.getAllEmployees();

		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905, "Retrieved Employees  successfully",
				"Employees list by status", employeeList, request.getRequestURI(), "Employee Details: Status", null);
		return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);
		

	}

	@RequestMapping(value = "/employee/searchCriteria", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Employee> getEmployeeRoleDataForSearchCriteria(@RequestParam("searchId") String searchId,
			@RequestParam("searchAttribute") String searchAttribute) throws MyTeamException {
		Employee employeesRole = empService.getEmployeeRoleDataForSearchCriteria(searchId, searchAttribute);
		return new ResponseEntity<>(employeesRole, HttpStatus.OK);
	}

	@RequestMapping(value = "/employee/autocomplete", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getEmployeeDetailsForAutocomplete() throws MyTeamException {
		List<String> details = empService.getEmployeeDetailsForAutocomplete();
		return new ResponseEntity<>(details, HttpStatus.OK);
	}

	@RequestMapping(value = "/employees/deliveryLeads/{domainId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDeliveryLeads(@PathVariable("domainId") String domainId, HttpServletRequest request)
			throws MyTeamException {
		List<HashMap<String, String>> managersList = empService.getDeliveryLeads(domainId);

		ResponseDetails getRespDetails = new ResponseDetails(new Date(), 905, "Retrieved Delivery Leads successfully",
				"Delivery Leads list", managersList, request.getRequestURI(), "Delivery Leads Details", null);
		return new ResponseEntity<ResponseDetails>(getRespDetails, HttpStatus.OK);

	}
	

	@RequestMapping(value = "/employees/active/sortByName", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Employee>> getActiveEmployeesSortByName() throws MyTeamException {
		List<Employee> employeesList = new ArrayList<>();
		if (empService.getActiveEmployees() != null) {
			employeesList = empService.getActiveEmployees().stream()
					.sorted((o1, o2) -> o1.getEmployeeName().trim().compareTo(o2.getEmployeeName().trim()))
					.collect(Collectors.toList());
		}
		return new ResponseEntity<>(employeesList, HttpStatus.OK);
	}


	@RequestMapping(value = "/getEmployeesHavingVisa", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEmployeesHavingVisa(@RequestParam("visa") String passport,HttpServletRequest request)
			throws MyTeamException {
		List<Employee> employees = new ArrayList<>();

		if (passport != null && !"passport".equalsIgnoreCase(passport)) {

			List<EmployeeVisa> employeeVisas = employeeVisaRepo.findByVisaName(passport);

			List<String> employeeIds = null;
			if (employeeVisas != null) {
				employeeIds = employeeVisas.stream().map(EmployeeVisa::getEmployeeId).collect(Collectors.toList());
			}
			if (employeeIds != null && !employeeIds.isEmpty()) {
				List<Employee> emps = empService.getActiveEmployees();
				for (Employee emp : emps) {
					if (employeeIds.contains(emp.getEmployeeId())) {
						employees.add(emp);
					}
				}
			}
		} else {
			if (empService.getActiveEmployees() != null) {
				employees = empService.getActiveEmployees().stream()
						.sorted((o1, o2) -> o1.getEmployeeName().compareTo(o2.getEmployeeName()))
						.collect(Collectors.toList());
			}

		}

		ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Resources have been retrieved successfully",
				"List of Resources who has visa", employees, request.getRequestURI(), "Resource details", null);
		return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);

	}

	@RequestMapping(value = "/subStatus", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> endSubStatus(@RequestBody EmployeeSubStatus employeeSubStatus, @RequestParam("loginEmpId") String loginEmpId ,HttpServletRequest request){

		ResponseDetails responseDetails = new ResponseDetails(new Date(), 602, "Sub status end updated successfully",
				"Emoployee sub status updated successfully", subStatusService.endSubStatus(loginEmpId,employeeSubStatus) , request.getRequestURI(), "Resource details", null);
		return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);

	}
	
	@RequestMapping(value = "/employeesBasedOnSubStatusForGivenDates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> employeesBasedOnSubStatusForGivenDates(@RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
																	@RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
																	@RequestParam("subStatus") String subStatus,
																	HttpServletRequest request){
		List<EmployeeSubStatusVO> employees = subStatusService.employeesBasedOnSubStatusForGivenDates(fromDate,toDate,subStatus);

		ResponseDetails responseDetails = new ResponseDetails(new Date(), 904, "Fetched Employees Successfully",
				"Fetched Employees Successfully", employees , request.getRequestURI(), "Resource details", null);
		return new ResponseEntity<ResponseDetails>(responseDetails, HttpStatus.OK);

	}
	@RequestMapping(value = "/employees/uploadProfile/{empid}", method = RequestMethod.POST)
	public ResponseEntity<Employee> handleFileUpload(@PathVariable("empid") String empid, @RequestParam(value = "file") MultipartFile file) throws MyTeamException, IOException {

		Employee employeeUpdated = empService.uploadProfile(empid, file);

		return new ResponseEntity<>(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/employees/downloadFile/{empId}", method = RequestMethod.GET)
	public void downloadFile(@PathVariable("empId") String empId, HttpServletResponse response) throws IOException {
		InputStream is = null;
		is = new ByteArrayInputStream(empService.getUploadFile(empId));
		response.setContentType("application/msword");
		byte[] bytes = new byte[1024];
		int bytesRead;

		while ((bytesRead = is.read(bytes)) != -1) {
			response.getOutputStream().write(bytes, 0, bytesRead);
		}
		is.close();
	}

}