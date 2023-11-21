package com.ouslsmartactivitydiary.item;

public class CourseItem {

    public static final int COURSE = 3;
    public static final int PROGRAMME = 4;
    public static final int LEVEL = 5;
    public static final int DIALOG_COURSE = 6;

    String courseCode, courseName, programme, mainDocId, level;
    private final int viewType;

    public CourseItem(int viewType, String courseCode, String courseName) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.viewType = viewType;
    }

    public CourseItem(String mainDocId, String programme, int viewType) {
        this.programme = programme;
        this.mainDocId = mainDocId;
        this.viewType = viewType;
    }

    public CourseItem(int viewType, String level) {
        this.level = level;
        this.viewType = viewType;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getViewType() {
        return viewType;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public String getMainDocId() {
        return mainDocId;
    }

    public void setMainDocId(String mainDocId) {
        this.mainDocId = mainDocId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
