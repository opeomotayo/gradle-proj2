package com.nisum.myteam.statuscodes;

public enum ResourceStatus {

    ACTIVE("Active"), IN_ACTIVE("InActive");
    private String status;

    private ResourceStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

}
