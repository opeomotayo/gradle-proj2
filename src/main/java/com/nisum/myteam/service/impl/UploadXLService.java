package com.nisum.myteam.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.service.IUploadXLService;
import com.nisum.myteam.utils.DataValidations;
import com.nisum.myteam.utils.MyTeamUtils;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UploadXLService implements IUploadXLService {

	@Autowired
	EmployeeService employeeService;

	@Autowired
    DataValidations dataValidations;

	@Override
	public String importDataFromExcelFile(MultipartFile file, String logInEmpId) throws MyTeamException {
		String result = "";
		try {
			PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().preferNullOverDefault(true)
					.datePattern("dd-MMM-yyyy").build();
			List<Employee> employees = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLS, Employee.class,
					options);
			if (!employees.isEmpty()) {
				result = allColumnsExistCheckInExcel(employees.get(MyTeamUtils.INT_ZERO));
				if (!result.isEmpty()) {
					log.info("Imported Excel file {} missing column {}", file.getOriginalFilename(), result);
					return result;
				}
				result = duplicateEmpIAndEmptyEmpIdsCheckInImportedExcel(employees);
				result += validateExcelRecords(employees);
				log.info("Partial Import :: Imported {} employee records from file: {}", employees.size(),
						file.getOriginalFilename());
				for (Employee employee : employees) {
					addEmployee(employee, logInEmpId);
				}
			} else {
				result = "Uploaded file: {" + file.getOriginalFilename() + "}, is Empty";
				log.info("Uploaded file: {}, is Empty", file.getOriginalFilename());
			}
			if (result.isEmpty()) {
				result = "Successfully Employees added";
				log.info("Full Import :: Imported {} employee records from file: {}", employees.size(),
						file.getOriginalFilename());
			}
		} catch (Exception e) {
			log.error("Exception occured while exporting the data from excel file due to: {}", e);
			throw new MyTeamException(
					"Exception occured while exporting the data from excel file due to :" + e.getMessage());
		}
		return result;
	}

	private String allColumnsExistCheckInExcel(Employee emp) {
		String resultString = "In excel sheet following coloumns are missing::";
		StringBuffer result = new StringBuffer(resultString);
		Method[] empMethodList = emp.getClass().getMethods();
		for (Method empMethod : empMethodList) {
			String mName = empMethod.getName();
			if (mName.startsWith("get") && isMethodInvocationRequired(mName)) {
				try {
					Object returnData = empMethod.invoke(emp);
					if (returnData == null) {
						mName = mName.substring(3, mName.length());
						result.append(mName).append(MyTeamUtils.CAMA);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		resultString = result.substring(resultString.length() - 1, result.length() - 1);
		if (resultString.length() > 0) {
			resultString = result.substring(0, result.length() - 1);
		}
		return resultString;
	}

	private boolean isMethodInvocationRequired(String mName) {
		boolean isRequired = true;
		if (mName.contains("getCreatedBy")) {
			isRequired = false;
		}
		if (mName.contains("getModifiedBy")) {
			isRequired = false;
		}
		if (mName.contains("getCreatedOn")) {
			isRequired = false;
		}
		if (mName.contains("getLastModifiedOn")) {
			isRequired = false;
		}
		if (mName.contains("getEndDate")) {
			isRequired = false;
		}
		if (mName.contains("getId")) {
			isRequired = false;
		}
		if (mName.contains("getClass")) {
			isRequired = false;
		}
		if (mName.contains("getPassportExpiryDate")) {
			isRequired = false;
		}
		if (mName.contains("getB1ExpiryDate")) {
			isRequired = false;
		}
		if (mName.contains("getDateOfBirth")) {
			isRequired = false;
		}
		return isRequired;
	}

	private String duplicateEmpIAndEmptyEmpIdsCheckInImportedExcel(List<Employee> employees) {
		int rowNum = MyTeamUtils.INT_TWO;
		StringBuffer emptyEmpIds = new StringBuffer();
		StringBuffer duplicteEmpIds = new StringBuffer();
		List<String> empIdsList = new ArrayList<String>();
		String result = "";
		for (Employee emp : employees) {
			String empId = emp.getEmployeeId().trim();
			if (empId.isEmpty()) {
				employees.remove(emp);
				emptyEmpIds.append(rowNum).append(MyTeamUtils.CAMA);
			} else if (empIdsList.contains(empId)) {
				employees.remove(emp);
				duplicteEmpIds.append(empId).append(MyTeamUtils.CAMA);
			} else {
				empIdsList.add(empId);// For, Duplicate check.
			}
			++rowNum;
		}

		if (emptyEmpIds.length() > 0) {
			result = "Below employee records have empty employee id :" + emptyEmpIds.toString();
		}
		if (duplicteEmpIds.length() > 0) {
			result += ":: \n Below employee records have duplicate employee Ids " + duplicteEmpIds.toString();
		}
		return result;
	}

	private String validateExcelRecords(List<Employee> employees) {
		List<String> inValidEmpRecList = new ArrayList<String>();
		String result = "";
		boolean mandatoryFlag = true;
		int rowNumber = 1;
		List<Employee> invalidEmpRecs = new ArrayList<Employee>();

		Set<String> empIdsSet = employees.stream().map(Employee::getEmployeeId).collect(Collectors.toSet());

		List<Employee> existingEmployess = employeeService.getEmployeesFromList(empIdsSet);

		if (existingEmployess.size() > MyTeamUtils.INT_ZERO) {
			result = "Below employee records already existed : \n"
					+ existingEmployess.stream().map(Employee::getEmployeeId).collect(Collectors.toSet()).toString();
			employees.removeAll(existingEmployess);
		}
		for (Employee emp : employees) {
			rowNumber += 1;
			mandatoryFlag = importExcelMandatoryColumnsValidation(emp);

			if (mandatoryFlag) {
				importExcelAdditionalColumnVAlidation(emp, inValidEmpRecList, invalidEmpRecs, rowNumber);
			} else {
				addInValidRecord(inValidEmpRecList, invalidEmpRecs, emp, rowNumber);
			}
		}
		if (invalidEmpRecs.size() > MyTeamUtils.INT_ZERO) {
			employees.removeAll(invalidEmpRecs);
			result += "Please check the following row number records : \n" + inValidEmpRecList.toString();
		}
		return result;
	}

	private boolean importExcelMandatoryColumnsValidation(Employee emp) {
		boolean mandatoryFlag = true;

		if (!dataValidations.validateNumber(emp.getEmployeeId())) {
			mandatoryFlag = false;
		} else if (!dataValidations.validateName(emp.getEmployeeName())) {
			mandatoryFlag = false;
		} else if (!dataValidations.isValidGender(emp.getGender())) {
			mandatoryFlag = false;
		} else if (!dataValidations.isValidDate(emp.getDateOfJoining())) {
			mandatoryFlag = false;
		} else if (!dataValidations.isValidFunctionalGroup(emp.getFunctionalGroup())) {
			mandatoryFlag = false;
		} else if (!dataValidations.isValidDesignation(emp.getDesignation())) {
			mandatoryFlag = false;
		} else if (!dataValidations.isValidWorkLocation(emp.getEmpLocation())) {
			mandatoryFlag = false;
		} else if (!dataValidations.isValidEmploymentType(emp.getEmploymentType())) {
			mandatoryFlag = false;
		} else if (!dataValidations.isValidRole(emp.getRole())) {
			mandatoryFlag = false;
		} else if (!dataValidations.isYesOrNo(emp.getHasPassort())) {
			mandatoryFlag = false;
		} else if (!dataValidations.isYesOrNo(emp.getHasB1())) {
			mandatoryFlag = false;
		} else if (!dataValidations.isValidEmail(emp.getEmailId())) {
			mandatoryFlag = false;
		}

		return mandatoryFlag;
	}

	private void importExcelAdditionalColumnVAlidation(Employee emp, List<String> inValidEmpRecList,
			List<Employee> invalidEmpRecs, int rowNumber) {
		if (!DataValidations.isAgeGreaterThanTwenty(emp.getDateOfBirth(), emp.getDateOfJoining())) {
			addInValidRecord(inValidEmpRecList, invalidEmpRecs, emp, rowNumber);
			return;
		}
		if (MyTeamUtils.YES.equalsIgnoreCase(emp.getHasPassort().trim())
				|| MyTeamUtils.STRING_Y.equalsIgnoreCase(emp.getHasPassort().trim())) {
			if (!dataValidations.isFutureDate(emp.getPassportExpiryDate())) {
				addInValidRecord(inValidEmpRecList, invalidEmpRecs, emp, rowNumber);
				return;
			}
		}

		if (emp.getHasPassort().trim().isEmpty() || MyTeamUtils.NO.equalsIgnoreCase(emp.getHasPassort().trim())
				|| MyTeamUtils.STRING_N.equalsIgnoreCase(emp.getHasPassort().trim())) {
			if (null != emp.getPassportExpiryDate()) {
				addInValidRecord(inValidEmpRecList, invalidEmpRecs, emp, rowNumber);
				return;
			}
		}

		if ((MyTeamUtils.YES.equals(emp.getHasB1()) || MyTeamUtils.STRING_Y.equals(emp.getHasB1()))
				&& !dataValidations.isFutureDate(emp.getB1ExpiryDate())) {
			addInValidRecord(inValidEmpRecList, invalidEmpRecs, emp, rowNumber);
			return;
		}
		if (!emp.getEmpStatus().trim().isEmpty()) {
			if (null != emp.getEndDate() && (emp.getEmpStatus().trim().equalsIgnoreCase(MyTeamUtils.IN_ACTIVE)
					|| emp.getEmpStatus().trim().equalsIgnoreCase(MyTeamUtils.IN_ACTIVE_SPACE)
					|| emp.getEmpStatus().trim().equalsIgnoreCase(MyTeamUtils.IN_HYPEN_ACTIVE_SPACE))) {
				addInValidRecord(inValidEmpRecList, invalidEmpRecs, emp, rowNumber);
				return;
			}
			if (emp.getEmpStatus().trim().equalsIgnoreCase(MyTeamUtils.ACTIVE) && null != emp.getEndDate()) {
				addInValidRecord(inValidEmpRecList, invalidEmpRecs, emp, rowNumber);
				return;
			}
		}
	}

	private void addEmployee(Employee employee, String empId) {
		try {
			if (employee.getRole().trim().isEmpty()) {
				employee.setRole(MyTeamUtils.EMPLOYEE);
			}
			if (employee.getEmploymentType().trim().isEmpty()) {
				employee.setEmploymentType(MyTeamUtils.FULL_TIME);
			}
			String empStatus = employee.getEmpStatus().trim();
			if (empStatus.isEmpty()) {
				if (null == employee.getEndDate()) {
					employee.setEmpStatus(MyTeamUtils.ACTIVE);
				} else {
					employee.setEmpStatus(MyTeamUtils.IN_ACTIVE_SPACE);
				}
			} else if ((empStatus.equalsIgnoreCase(MyTeamUtils.IN_ACTIVE)
					|| empStatus.equalsIgnoreCase(MyTeamUtils.IN_ACTIVE_SPACE)
					|| empStatus.equalsIgnoreCase(MyTeamUtils.IN_HYPEN_ACTIVE_SPACE))
					&& null != employee.getEndDate()) {
				employee.setEmpStatus(MyTeamUtils.IN_ACTIVE_SPACE);
			}
			if (!employee.getGender().trim().isEmpty()) {
				if (employee.getGender().equalsIgnoreCase(MyTeamUtils.MALE)) {
					employee.setGender(MyTeamUtils.MALE);
				} else if (employee.getGender().equalsIgnoreCase(MyTeamUtils.FEMALE)) {
					employee.setGender(MyTeamUtils.FEMALE);
				}
			}
			employeeService.createEmployee(employee, empId);
		} catch (MyTeamException e) {
			e.printStackTrace();
		}
	}

	private void addInValidRecord(List<String> inValidEmpRecList, List<Employee> invalidEmpRecs, Employee emp,
			int rowNumber) {
		inValidEmpRecList.add(Integer.toString(rowNumber));
		invalidEmpRecs.add(emp);
	}

}
