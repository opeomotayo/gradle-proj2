package com.nisum.myteam.statuscodes;

public enum ResourceAllocationStatus {


    TRAINEE("Trainee"), RESERVED("Reserved"), BILLABLE("Billable"), NON_BILLABLE("Non-Billable");

    private String status;

    private ResourceAllocationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }


}
