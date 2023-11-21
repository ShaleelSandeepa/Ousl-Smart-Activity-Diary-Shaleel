package com.ouslsmartactivitydiary.item;

import java.util.Map;

public class StaffItem {

    String staffID;
    String staffName, staffEmail;
    String staffCourses;

    public StaffItem(String staffID, String staffName, String staffEmail, String staffCourses) {
        this.staffID = staffID;
        this.staffName = staffName;
        this.staffEmail = staffEmail;
        this.staffCourses = staffCourses;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }

    public String getStaffCourses() {
        return staffCourses;
    }

    public void setStaffCourses(String staffCourses) {
        this.staffCourses = staffCourses;
    }
}
