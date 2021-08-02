package com.nisum.myteam.utils.constants;

public enum RoleConstant {


   DIRECTOR("","Director"),DELIVERY_MANAGER("","Delivery Manager"),
    MANAGER("","Manager"),HR_MANAGER("","HR Manager"),LEAD("","Lead");
    private String roleId;
    private String roleName;

    private RoleConstant(String roleId, String roleName) {
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
