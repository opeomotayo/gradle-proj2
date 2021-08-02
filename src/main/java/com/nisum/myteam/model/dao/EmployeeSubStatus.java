package com.nisum.myteam.model.dao;


import com.nisum.myteam.model.AuditFields;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "employeeSubStatus")
public class EmployeeSubStatus extends AuditFields {

    @Id
    private ObjectId id;
    @NonNull
    private String employeeID;
    @NonNull
    private String subStatus;
    @NonNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date fromDate;
    @NonNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date toDate;
}
