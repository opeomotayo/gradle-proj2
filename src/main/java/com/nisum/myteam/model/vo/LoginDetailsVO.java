package com.nisum.myteam.model.vo;

import com.nisum.myteam.model.EmployeeLoginData;
import com.nisum.myteam.model.dao.EffectiveLoginData;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoginDetailsVO {
    private String employeeId;
    private String employeeName;
    private String emailId;
    private List<EffectiveLoginData> effectiveLoginData;
    private List<EmployeeLoginData> employeeLoginDataList;
    private String avgHours;
    private String orphanLogin;
    private String functionalGroup;
    private String deliverManager;

}
