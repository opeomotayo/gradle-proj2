package com.nisum.myteam.model.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.nisum.myteam.model.AuditFields;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

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
@Document(collection = "projects")
public class Project extends AuditFields implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;
    private String projectId;
    private String projectName;
    private String domainId;
    private String accountId;
    private String status;
    private List<String> employeeIds;
    private List<String> managerIds;

    @DateTimeFormat(iso = ISO.DATE)
    private Date projectStartDate;
    @DateTimeFormat(iso = ISO.DATE)
    private Date projectEndDate;
    private List<String> deliveryLeadIds;
}
