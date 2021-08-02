package com.nisum.myteam.utils.constants;


public enum Shifts {

    SHIFT1("Shift 1(9:00 AM - 6:00 PM)"),
    SHIFT2("Shift-2(2:00 PM - 11:00 PM)"),
    SHIFT3("Shift 3(10:00 PM - 6:00 AM)"),
    SHIFT4("Shift 4(7:30 AM - 3:30 PM)"),
    SHIFT5("Shift 5(11:30 AM - 7:30 PM)");

    private String shiftType;

    private Shifts(String shiftType) {
        this.shiftType = shiftType;
    }

    public String getShiftType() {
        return this.shiftType;
    }
}
