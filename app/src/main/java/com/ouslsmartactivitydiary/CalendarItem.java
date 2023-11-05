package com.ouslsmartactivitydiary;

import android.widget.ImageView;
import android.widget.TextView;

public class CalendarItem {

    public static final int ACTIVITIES = 1;
    public static final int CALENDAR = 2;

    private final int viewType;

    int activityIcon;
    String courseCode, activityName, date, day;

    // calendar activities constructor
    public CalendarItem(int viewType, int activityIcon, String courseCode, String activityName, String date, String day) {
        this.viewType = viewType;
        this.activityIcon = activityIcon;
        this.courseCode = courseCode;
        this.activityName = activityName;
        this.date = date;
        this.day = day;
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
}
