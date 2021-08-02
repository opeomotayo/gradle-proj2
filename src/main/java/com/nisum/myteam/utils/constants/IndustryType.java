package com.nisum.myteam.utils.constants;

public enum IndustryType {

    RETAIL("retail"),FINANTIAL("Financial");

    private String type;

    private IndustryType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
