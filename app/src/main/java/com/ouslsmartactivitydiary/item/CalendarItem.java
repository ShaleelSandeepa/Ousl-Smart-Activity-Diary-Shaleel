package com.ouslsmartactivitydiary.item;

import java.util.Date;

public class CalendarItem {

    public static final int ACTIVITIES = 1;
    public static final int CALENDAR = 2;
    public static final int COURSE_WISE = 3;

    public static final int MONDAY = 11;
    public static final int TUESDAY = 22;
    public static final int WEDNESDAY = 33;
    public static final int THURSDAY = 44;
    public static final int FRIDAY = 55;
    public static final int SATURDAY = 66;
    public static final int SUNDAY = 77;

    private final int viewType;

    int activityIcon;
    String courseCode, activityName, date, day, category, id, note;
    Date timeStamp, endTime;

    // calendar activities constructor
    public CalendarItem(int viewType, String category, String id, int activityIcon, String courseCode,
                        String activityName, String date, String day, Date timeStamp, Date endTime, String note) {
        this.viewType = viewType;
        this.category = category;
        this.id = id;
        this.activityIcon = activityIcon;
        this.courseCode = courseCode;
        this.activityName = activityName;
        this.date = date;
        this.day = day;
        this.timeStamp = timeStamp;
        this.endTime = endTime;
        this.note = note;
    }

    // calendar activities constructor
    public CalendarItem(int viewType, String id, int activityIcon){
        this.viewType = viewType;
        this.id = id;
        this.activityIcon = activityIcon;
    }

    // calendar activities getters and setters

    public int getViewType() {
        return viewType;
    }

    public int getActivityIcon() {
        return activityIcon;
    }

    public void setActivityIcon(int activityIcon) {
        this.activityIcon = activityIcon;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
