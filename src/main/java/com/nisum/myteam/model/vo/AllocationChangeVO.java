package com.nisum.myteam.model.vo;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AllocationChangeVO {


    private String employeeId;

    private String employeeName;

    private String prevAccountName;

    private String prevProjectName;

    private String prevBillingStatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date prevBillingStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date prevBillingEndDate;

    private String currentAccountName;
    private String currentProjectName;
    private String currentBillingStatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date currentBillingStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date currentBillingEndDate;


}
