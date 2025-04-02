package com.example.sheduleapp_v5.models;

public class LessonItem {
    private String lessonName;
    private String teacherName;
    private String classroom;
    private String comment;
    private String subgroup;
    private Integer weekType;

    public String getLessonName() {
        return lessonName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getClassroom() {
        return classroom;
    }

    public String getComment() {
        return comment;
    }

    public String getSubgroup() {
        return subgroup;
    }

    public Integer getWeekType() {
        return weekType;
    }
}
