package com.example.sheduleapp_v5.models;

import com.google.gson.annotations.SerializedName;

public class LessonItem {
    @SerializedName("lessonName")
    private String lessonName;
    @SerializedName("teacherName")
    private String teacherName;
    @SerializedName("classroom")
    private String classroom;
    @SerializedName("comment")
    private String comment;
    @SerializedName("subgroup")
    private String subgroup;
    @SerializedName("weekType")
    private Integer weekType;
    @SerializedName("location")
    private String location;
    @SerializedName("groupName")
    private String groupName;

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

    public String getLocation() {
        return location;
    }

    public String getGroupName() {
        return groupName;
    }
}
