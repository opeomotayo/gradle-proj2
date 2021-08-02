package com.nisum.myteam.exception.handler;

public class EmployeeRecordsNotFound extends RuntimeException {
  public EmployeeRecordsNotFound(Integer id) {
    super("There is a no records for this employee");
  }
}
