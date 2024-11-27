package com.example.sheduleapp_v5.models;

import java.util.List;

public class Lesson {
    private int lessonIndex;
    private String lessonStartTime;
    private String lessonEndTime;
    private List<LessonItem> items;

    public int getLessonIndex() {
        return lessonIndex;
    }
    public void setLessonIndex(int lessonIndex) {
        this.lessonIndex = lessonIndex;
    }

    public String getLessonStartTime() {
        return lessonStartTime;
    }

    public void setLessonStartTime(String lessonStartTime) {
        this.lessonStartTime = lessonStartTime;
    }

    public String getLessonEndTime() {
        return lessonEndTime;
    }
    public void setLessonEndTime(String lessonEndTime) {
        this.lessonEndTime = lessonEndTime;
    }

    public List<LessonItem> getItems() {
        return items;
    }
    public void setItems(List<LessonItem> items) {
        this.items = items;
    }
}
