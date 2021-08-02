package com.nisum.myteam.model.vo;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MyProjectAllocationVO {

    private String projectId;
    private String projectName;
    private String accountName;

    private String billableStatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date billingStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date billingEndDate;

    private String shift;
    private String resourceStatus;

    private List<HashMap<String, String>> deliveryLeadIds;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date projectStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date projectEndDate;

    private String projectStatus;


}
