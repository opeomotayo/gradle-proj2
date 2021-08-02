package com.nisum.myteam.statuscodes;

public enum EmployeeStatus {

    ACTIVE("Active");
    private String status;

    private EmployeeStatus(String status) {
        this.status = status;
    }


    public String getStatus() {
        return this.status;
    }

}
