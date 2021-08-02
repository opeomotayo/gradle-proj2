package com.nisum.myteam.model.dao;

import com.nisum.myteam.model.AuditFields;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "resourceAllocation")
public class Resource extends AuditFields implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;

    private String employeeId;

    private String projectId;

    @DateTimeFormat(iso = ISO.DATE)
    private String billableStatus;


    @DateTimeFormat(iso = ISO.DATE, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Date billingStartDate;

    @DateTimeFormat(iso = ISO.DATE, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Date billingEndDate;

    private String resourceRole;
    private String status;
    private String onBehalfOf;

}
