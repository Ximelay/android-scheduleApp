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

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSubgroup() {
        return subgroup;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getWeekType() {
        return weekType;
    }

    public void setWeekType(Integer weekType) {
        this.weekType = weekType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setSubgroup(String subgroup) {
        this.subgroup = subgroup;
    }
}
