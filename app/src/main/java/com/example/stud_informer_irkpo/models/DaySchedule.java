package com.example.stud_informer_irkpo.models;

import java.util.List;

public class DaySchedule {
    private String dayOfWeek;
    private int weekType;
    private List<LessonIndex> lessonIndexes; // Уроки на этот день

    // Геттеры
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public int getWeekType() {
        return weekType;
    }

    public List<LessonIndex> getLessonIndexes() {
        return lessonIndexes;
    }
}
