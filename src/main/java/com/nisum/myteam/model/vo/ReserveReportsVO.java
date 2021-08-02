package com.nisum.myteam.model.vo;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReserveReportsVO {


    private String employeeId;

    private String employeeName;

    private String accountName;

    private String projectName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date billingStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date billingEndDate;


}
