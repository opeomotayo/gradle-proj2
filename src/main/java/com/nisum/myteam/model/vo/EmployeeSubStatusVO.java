package com.nisum.myteam.model.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EmployeeSubStatusVO {
	private String empId;
	private String empName;
	private String empStatus;
	private Date fromDate;
	private Date toDate;
	private String functionalGroup;

	
}
