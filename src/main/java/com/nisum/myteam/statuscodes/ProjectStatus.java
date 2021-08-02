package com.nisum.myteam.statuscodes;

public enum ProjectStatus {

    CREATE(700, "Project has been created"),
    UPDATE(701, "Project has been updated"),
    DELETE(702, "Project has been deleted successfully"),
    ALREADY_EXISTED(703, "Project is already existed"),
    PROJECTID_IS_NOT_EXISTS(704,"Project Id is not existed"),
    PROJECT_NAME_IS_NOT_EXISTS(705,"Project is not existed"),
    IS_NOT_FOUND(706, "Project is Not found"),
    GET_PROJECTS(707, "Retrieved the projects successfully"),
    EMPLOYEE_NOT_EXISTS(708,"Please Provide valid employee id"),
    DELIVERYLEAD_NOT_EXISTS(709,"Please Provide valid Delivery lead id");

    private int code;
    private String message;

    private ProjectStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
