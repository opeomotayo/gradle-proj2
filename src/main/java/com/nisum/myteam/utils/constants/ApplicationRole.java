package com.nisum.myteam.utils.constants;

public enum ApplicationRole {

    FUNCTIONAL_MANAGER("", "FM"), ADMIN("", "Admin"), DIRECTOR("", "Director"), DELIVERY_MANAGER("DM", "Delivery Manager"), DELIVERY_LEAD("DL", "Delivery Lead"), LEAD("L", "Lead"),
    EMPLOYEE("", "Employee"), HR("", "Hr");

    private String roleId;
    private String roleName;

    private ApplicationRole(String roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;


    }

    public String getRoleId() {
        return this.roleId;
    }

    public String getRoleName() {
        return this.roleName;
    }
}
