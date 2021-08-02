package com.nisum.myteam.model.dao;

import com.nisum.myteam.model.AuditFields;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(collection = "employeeShifts")
public class EmployeeShift extends AuditFields {

	@Id
	private String id;
	private String employeeId;
	private String employeeName;
	private String shift;
	//private Date createDate;
	//private Date updatedDate;
	private boolean active;

}
