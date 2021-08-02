package com.nisum.myteam.controller;


import com.nisum.myteam.fileexport.GenerateStatusExcelReport;
import com.nisum.myteam.fileexport.GenerateStatusPdfReport;
import com.nisum.myteam.model.Book;
import com.nisum.myteam.model.FromToDates;
import com.nisum.myteam.model.dao.MyStatus;
import com.nisum.myteam.model.StatusResponse;
import com.nisum.myteam.service.MyStatusService;
import com.nisum.myteam.utils.MyTeamDateUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
//@RequestMapping("/mystatus")
public class MyStatusController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyStatusController.class);

  @Autowired
  private MyStatusService statusService;

  /**
   * Save the task details
   * @param status
   * @return
   */
  //@PostMapping("/saveStatusDetails")
  @RequestMapping(value = "saveStatusDetails", method = RequestMethod.POST)

  public ResponseEntity<StatusResponse> saveStatusDetails(@RequestBody MyStatus status) {
    MyStatus saveResponse = null;
    LOGGER.info("Creating the employee status for taskType: " + status.getTaskType());
    StatusResponse response = new StatusResponse();
    response.setStatusCode(400);
    response.setTitle("Add Task Details");
    response.setMessage("Something went wrong please try again");
    try {
      if (status != null) {
        int totalTime = 0;
        if (status.getHoursSpent() != null && status.getTaskDate() != null) {
          totalTime = hoursSpentCal(status.getEmpId(), MyTeamDateUtils.convertUtilDateToLocalDate(status.getTaskDate()));
          String hours = status.getHoursSpent();
          totalTime = totalTime + getTotalMinutes(hours);
          if (totalTime <= 840) {
            saveResponse = statusService.saveStatusDetails(status);
            response.setMessage("Task details saved successfully!");
            response.setStatusCode(200);
          } else {
            response.setMessage("Entered number of hours more than 14:00 hrs");
            response.setStatusCode(400);
          }
        }
      }
    } catch (Exception exp) {
      LOGGER.error("Getting exception while saving the details in database : " + exp);
    }
    return ResponseEntity.ok().body(response);
  }

  /**
   * Fetch all the employees task details
   * @return
   */
  @GetMapping("/getAllEmployeesStatusDetails")
  public ResponseEntity<List<MyStatus>> getEmployeeStatusDetails() {
    List<MyStatus> statusResponse = null;
    try {
      statusResponse = statusService.getEmployeeStatusDetails();
    } catch (Exception ex) {
      LOGGER.error("Getting exception while fetching the details from database : " + ex);
      throw ex;
    }
    return ResponseEntity.ok().body(statusResponse);
  }

  /**
   * Updating the status based on task ID
   * @param id
   * @param status
   * @return
   */
  @PutMapping("/updateStatusDetails/{id}")
  public StatusResponse updateEmployeeStatusDetails(@PathVariable Integer id, @RequestBody MyStatus status) {
    MyStatus statusUpdated = null;
    LOGGER.info("Updating the employee status for Id: " + id);
    status.setId(id);
    StatusResponse response = new StatusResponse();
    response.setStatusCode(200);
    response.setMessage("Task details updated successfully!");
    response.setTitle("Update Task Details");
    try {
      statusUpdated = statusService.updateStatusDetails(status);
    } catch (Exception ex) {
      LOGGER.error("Getting exception while updating the status : " + ex);
      response.setStatusCode(400);
      response.setMessage("Something went wrong, please try again");
      response.setTitle("Update Task Details");
    }
    return response;
  }

  /**
   * Dalete the task details based on task ID
   * @param id
   * @return response
   */
  @DeleteMapping("/deleteStatusDetails/{id}")
  public StatusResponse deleteEmployeeStatusDetails(@PathVariable Integer id) {
    LOGGER.info("Deleting the employee status for Id: " + id);
    StatusResponse response = new StatusResponse();
    response.setStatusCode(200);
    response.setMessage("Task details deleted successfully!");
    response.setTitle("Delete Task Details");
    try {
      statusService.deleteEmployeeStatusDetails(id);
    } catch (Exception ex) {
      LOGGER.error("Getting exception while deleting the status : " + ex);
      response.setStatusCode(400);
      response.setMessage("Something went wrong please try again");
    }
    return response;
  }

  /**
   * Fetch the last five dates records
   * @return
   */
  @GetMapping("/getDefaultStatusDetails/{empId}")
  public ResponseEntity<List<MyStatus>> getDefalutStatusDetails(@PathVariable String empId) {
    List<MyStatus> statusResponse = null;
    try {
      LOGGER.info("Retrieving the employee last five days task details for EmpId : " + empId);
      statusResponse = statusService.getDefaultStatusDetails(empId);
    } catch (Exception e) {
      LOGGER.error("Geeting exception while fetching the data : " + e);
      e.printStackTrace();
    }
    return ResponseEntity.ok().body(statusResponse);
  }

  /**
   * Generate Excel sheet and download the file
   * @param response
   * @throws IOException
   */
  @GetMapping("/download/excelReport")
  public void downloadExcelFormat(HttpServletResponse response) throws IOException {
    response.setContentType("application/octet-stream");
    response.setHeader("Content-Disposition", "attachment; filename=status-report.xlsx");
    List<MyStatus> statusResponse = null;
    try {
      statusResponse = statusService.getEmployeeStatusDetails();
    } catch (Exception ex) {
      LOGGER.error("Getting exception while fetching the details from database : " + ex);
      throw ex;
    }
    ByteArrayInputStream stream = GenerateStatusExcelReport.generateExcelStatusReport(statusResponse);
    IOUtils.copy(stream, response.getOutputStream());
  }

  @GetMapping("/getAllEvents/{empId}")
  public List<MyStatus> getByDateRange(@PathVariable String empId, @RequestParam(value = "fromDate", required = false) LocalDate fromDate, @RequestParam(value = "toDate", required = false) LocalDate toDate) {
    LOGGER.info("Retrieving the employee status details for Id: " + empId);
    List<MyStatus> empResponse = null;
    try {
      if (StringUtils.isNotBlank(empId)) {
        if (fromDate != null && toDate != null){}
          //empResponse = statusService.getByDateRange(fromDate, toDate, empId);
        //else
         // empResponse = statusService.getStatusDetailsByEmpId(empId);
      }
    } catch (Exception exp) {
      LOGGER.error("Getting exception while retrieving the Details : " + exp);
      throw exp;
    }
    return empResponse;
  }

  /**
   * Fetch the task details based on date range(fromDate to toDate)
   * @param empId
   * @param dates
   * @return
   */
  @PostMapping(value = "getStatusDetailsByDateRange/{empId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public List<MyStatus> getEmpStatus(@PathVariable String empId, @RequestBody FromToDates dates) {
    LOGGER.info("Retrieving the employee task details for " + empId + " from date : " + dates.getFromDate() + " to date : " + dates.getToDate());
    List<MyStatus> empResponse = null;
    try {
      if (StringUtils.isNotBlank(empId) && dates.getFromDate() != null && dates.getToDate() != null) {
        empResponse = statusService.getByDateRange(dates.getFromDate(), dates.getToDate(), empId);
      }
    } catch (Exception e) {
      LOGGER.error("Getting exception while fetching the date " + e.getMessage());
      e.printStackTrace();
    }
    return empResponse;
  }

  /**
   * Generate PDF file
   * @return
   * @throws IOException
   */
  @RequestMapping(value = "/download/pdfReport", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<InputStreamResource> downloadPdfFormat() throws IOException {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "inline; filename=StatusReport.pdf");
    List<MyStatus> statusResponse = null;
    try {
      statusResponse = statusService.getEmployeeStatusDetails();
    } catch (Exception ex) {
      LOGGER.error("Getting exception while fetching the details from database : " + ex);
      throw ex;
    }
    ByteArrayInputStream bis = GenerateStatusPdfReport.statusReport(statusResponse);
    return ResponseEntity
      .ok()
      .headers(headers)
      .contentType(MediaType.APPLICATION_PDF)
      .body(new InputStreamResource(bis));
  }

  /**
   * Fetch the task details based on employee ID
   * @param empId
   * @return
   */
  @GetMapping("/findTodayStatusByEmpId/{empId}")
  public List<MyStatus> findTodayStatus(@PathVariable String empId) {
    LOGGER.info("Fetching the today status details using EmpId : " + empId);
    String response = null;
    StatusResponse error = new StatusResponse();
    error.setTitle("Status Details");
    List<MyStatus> status = null;
    try {
      status = statusService.findTodayStatus(empId);
      if (status == null) {
        error.setMessage("No data found for today status");
        response = error.toString();
      }else {
        response = status.toString();
        LOGGER.info("Employee :: "+ empId +" Today Status :: "+response);
      }
    } catch (Exception e) {
      LOGGER.error("Getting exception while fetching the today status details from database : " + e);
      error.setStatusCode(400);
      error.setMessage("Something went wrong, please try again");
      response = error.toString();
    }
    return status;
  }


  /**
   * Find number of hours by task date and employee id
   * @param empId
   * @return totalTime
   */
  public int hoursSpentCal(String empId, LocalDate today) {
    int totalTime = 0;
    List<MyStatus> taskData = statusService.findTaskDataByTaskDate(empId, today);
    if (taskData != null) {
      for (MyStatus st : taskData) {
        if (st.getHoursSpent() != null) {
          String time = st.getHoursSpent();
          String[] t = time.split(":");
          totalTime = totalTime + Integer.valueOf(t[0]) * 60 + Integer.valueOf(t[1]);
        }
      }
    }
    return totalTime;
  }
/**
 * Get total minutes by passing hours
 */
  public static int getTotalMinutes(String time) {
    String[] t = time.split(":");
    return Integer.valueOf(t[0]) * 60 + Integer.valueOf(t[1]);
  }

  List<Book> book = new ArrayList<>();
  @PostMapping("bookdata")
  public List<Book> savebook(@RequestBody Book bookdata){
    book.add(bookdata);
    return book;
  }
}
