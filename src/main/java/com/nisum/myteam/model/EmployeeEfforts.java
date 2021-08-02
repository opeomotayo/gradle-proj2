package com.nisum.myteam.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmployeeEfforts implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
	private String employeeId;
	private String employeeName;
	private String totalHoursSpentInWeek;
	private String projectName;
	private String accountName;
	private String functionalOrg;
}
