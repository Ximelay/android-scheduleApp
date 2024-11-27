package com.example.sheduleapp_v5.models;

import java.util.List;

public class DaySchedule {
    private String dayOfWeek;
    private List<Lesson> lessonIndexes;

    public String getDayOfWeek() {
        return dayOfWeek;
    }
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public List<Lesson> getLessonIndexes() {
        return lessonIndexes;
    }
    public void setLessonIndexes(List<Lesson> lessonIndexes) {
        this.lessonIndexes = lessonIndexes;
    }
}
