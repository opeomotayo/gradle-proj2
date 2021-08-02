package com.nisum.myteam.statuscodes;

public enum DomainStatus {

    CREATE(610, "Domain has been created"),
    UPDATE(611, "Domain has been updated"),
    ALREADY_EXISTED(612,"Domain is already existed"),
    IS_NOT_FOUND(613,"Domain is Not found"),
    GET_DOMIAINS(614,"Retrieved the domains successfully");
    private int code;
    private String message;

    private DomainStatus(int code, String message) {
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
