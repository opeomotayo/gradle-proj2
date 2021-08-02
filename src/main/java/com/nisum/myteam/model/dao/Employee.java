package com.nisum.myteam.model.dao;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.poiji.annotation.ExcelCellName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "employees")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;
    
    
    @NotBlank(message="Employee Id cannot be blank")
    //@Pattern(regexp = "^\\d{6}", message = "Invalid Employee code, It should be 5 digit")
    @ExcelCellName("Employee ID")
    private String employeeId;

    @NotBlank(message="Employee name should not be empty")
    @Size(min = 2, max = 80, message = "Employee Name should have atlast 2 and less than 200 characters")
    @ExcelCellName("Employee Name")
    private String employeeName;

    
    @Email(message = "Invalid email id")
    @ExcelCellName("Email ID")
    private String emailId;

    @ExcelCellName("Role")
    private String role;

    @ExcelCellName("Shift")
    private String shift;
    
    @NotBlank(message="Designation cannot be blank")
    @ExcelCellName("Designation")
    private String designation;


    @ExcelCellName("Primary Skill")
    private String baseTechnology;

//    @ExcelCellName("Domain")
//    private String domain;

    @ExcelCellName("Location")
    private String empLocation;

    @ExcelCellName("Skills")
    private String technologyKnown;
    
  
    //@Pattern(regexp="(^$|[0-9]{10})",message="Invalid mobile number")
    @ExcelCellName("Primary Mobile")
    private String mobileNumber;

   
    @Pattern(regexp="(^$|[0-9]{10})",message="Invalid alternate mobile number")
    @ExcelCellName("Alternate Mobile")
    private String alternateMobileNumber;

    @Email(message = "Invalid personal email id")
    @ExcelCellName("Personal Email")
    private String personalEmailId;

    @ExcelCellName("Functional Group")
    private String functionalGroup;

    @ExcelCellName("Employment Status")
    private String empStatus;
    
    @ExcelCellName("Employment Sub Status")
    private Object empSubStatus;

    @ExcelCellName("Employment Type")
    private String employmentType;

    @NotNull
    @ExcelCellName("Date Of Joining")
    private Date dateOfJoining;

    
    @ExcelCellName("Date Of Birth")
    private Date dateOfBirth;

    @ExcelCellName("Gender")
    private String gender;

    @ExcelCellName("Has Passport")
    private String hasPassort;

    @ExcelCellName("Passport Expiry Date")
    private Date passportExpiryDate;

    @ExcelCellName("Has B1")
    private String hasB1;

    @ExcelCellName("B1 Expiry Date")
    private Date b1ExpiryDate;

    @ExcelCellName("Created")
    private Date createdOn;

    @ExcelCellName("Last Modified")
    private Date lastModifiedOn;

    @ExcelCellName("Exit Date")
    private Date endDate;
    
    private String createdBy;
    
    private String modifiedBy;
    private String country;
    private Date lastUpdateProfile;
    @Field
    private byte[] updateProfile;



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((employeeId == null) ? 0 : employeeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (employeeId == null) {
			if (other.employeeId != null)
				return false;
		} else if (!employeeId.equals(other.employeeId))
			return false;
		return true;
	}
    
    

}
