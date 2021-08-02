package com.nisum.myteam.model.vo;


import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChangedResourceVO {
    private String emplyeeId;
    private String employeeName;
    private String prevBillingStatus;
    private String prevClient;
    private String prevProject;
    private Date prevBillingStartingDate;
    private Date prevBillingEndDate;
    private String currentBillingStatus;
    private String currentClient;
    private String currentProject;
    private Date currentBillingStartingDate;
    private Date currentBillingEndDate;
}
