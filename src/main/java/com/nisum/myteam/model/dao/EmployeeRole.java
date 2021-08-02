package com.nisum.myteam.model.dao;

import java.io.Serializable;
import org.bson.types.ObjectId;
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
//@Document(collection = "EmpAssignedRoleMappingInfo")
@Document(collection = "employeeRoles")
public class EmployeeRole implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private ObjectId id;
	private String employeeId;
	private String roleId;
	private String isActive;

}
