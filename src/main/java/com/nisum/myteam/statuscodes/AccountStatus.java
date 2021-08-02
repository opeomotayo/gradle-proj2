package com.nisum.myteam.statuscodes;


public enum AccountStatus {

    CREATE(600, "Account has been created"),
    UPDATE(601, "Account has been updated"),
    DELETE(602, "Account has been deleted successfully"),
    GET_ACCOUNTS(603, "Retrieved the accounts successfully"),
    GET_ACCOUNT(604, "Retrieved the account successfully"),
    GET_ACCOUNT_NAMES(605, "Retrieved the account names successfully"),
    ALREADY_EXISTED(606, "An Account is already existed"),
    NOT_FOUND(607, "Account is Not found"),
    NOT_VALID_ID(608, "AccountId is not valid");

    private int code;
    private String message;
    private String description;

    private AccountStatus(int code, String message) {
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