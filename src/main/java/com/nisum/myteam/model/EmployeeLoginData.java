package com.nisum.myteam.model;

import lombok.*;

import java.sql.Time;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmployeeLoginData {

    private String employeeId;
    private String name;
    private Date date;
    private Time time;
    private String EntryType;
    private String entryDoor;

}
