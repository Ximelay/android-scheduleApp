package com.example.sheduleapp_v5.models;

public class LessonItem {
    private int groupId;
    private String groupName;
    private Integer weekType;
    private String lessonName;
    private String subgroup;
    private String classroom;
    private String location;
    private String teacherId;
    private String teacherName;
    private String comment;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;

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

    public String getLessonName() {
        return lessonName;
    }
    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getSubgroup() {
        return subgroup;
    }
    public void setSubgroup(String subgroup) {
        this.subgroup = subgroup;
    }

    public String getClassroom() {
        return classroom;
    }
    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getTeacherId() {
        return teacherId;
    }
    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
