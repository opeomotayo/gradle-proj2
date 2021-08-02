package com.nisum.myteam.utils.constants;


public enum ProjectStatus {
    ACTIVE("Active"),
    INACTIVE("InActive"),
    COMPLETED("Completed"),
    ONHOLD("On Hold");

    private String projectStatus;

    private ProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getProjectStatus() {
        return this.projectStatus;
    }
}
