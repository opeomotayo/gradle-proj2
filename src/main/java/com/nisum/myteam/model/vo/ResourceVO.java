package com.nisum.myteam.model.vo;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResourceVO {

    @Id
    private ObjectId id;
    private String employeeId;
    private String employeeName;
    private String designation;
    private String emailId;
    private String projectId;
    private String projectName;
    private String status;
    private String onBehalfOf;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private String billableStatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Date billingStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Date billingEndDate;

    private String resourceStatus;
    private String resourceRole;

    private String mobileNo;

    private String accountName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Date projectStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Date projectEndDate;

    private String projectStatus;


}
