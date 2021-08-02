package com.nisum.myteam.model;

import java.io.Serializable;
import java.util.Date;

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
public class Reports implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String employeeId;
	private String employeeName;
	private String emailId;
	private String projectName;
	private String billableStatus;
	private Date billingStartDate;
	private Date billingEndDate;
	private String functionalGroup;
	private String onBehalfOf;
	
}
