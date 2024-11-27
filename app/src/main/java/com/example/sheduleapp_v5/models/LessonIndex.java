package com.example.sheduleapp_v5.models;

import java.util.List;

public class LessonIndex {
    private String lessonStartTime;
    private String lessonEndTime;
    private List<LessonItem> items; // Список уроков в этом индексе

    public String getLessonStartTime() {
        return lessonStartTime;
    }

    public String getLessonEndTime() {
        return lessonEndTime;
    }

    public List<LessonItem> getItems() {
        return items;
    }
}
