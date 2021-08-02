package com.nisum.myteam.model.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.poiji.annotation.ExcelCellName;

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
@Document(collection = "employeeLocations")
public class EmployeeLocation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private ObjectId id;
	private String employeeId;
	private String employeeName;
	private String empLocation;
	private Date startDate;
	private Date endDate;
	private Date createDate;
	private Date updatedDate;
	private boolean active;

}
