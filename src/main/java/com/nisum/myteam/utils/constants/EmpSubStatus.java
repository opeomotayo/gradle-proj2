package com.nisum.myteam.utils.constants;


public enum EmpSubStatus {

    LONG_LEAVE("Long Leave"),
    MATERNITY_LEAVE("Maternity Leave"),
    ONSITE_TRAVEL("Onsite Travel"),
    RESIGNED("Resigned"),
    AT_CLIENT_LOCATION("At Client Location");

    private String leaveType;

    private EmpSubStatus(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getLeaveType() {
        return this.leaveType;
    }
}
