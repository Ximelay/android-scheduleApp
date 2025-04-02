package com.example.sheduleapp_v5;


import com.example.sheduleapp_v5.models.LessonItem;

import java.util.List;

public class DisplayLessonItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_LESSON = 1;
    private int type;

    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private List<LessonItem> lessons;
    private boolean isFirstOfDay;
    private int currentWeekType; // <--- новое поле

    public DisplayLessonItem(String dayOfWeek, String startTime, String endTime, List<LessonItem> lessons, boolean isFirstOfDay, int currentWeekType) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lessons = lessons;
        this.isFirstOfDay = isFirstOfDay;
        this.currentWeekType = currentWeekType;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public List<LessonItem> getLessons() {
        return lessons;
    }

    public boolean isFirstOfDay() {
        return isFirstOfDay;
    }

    public int getCurrentWeekType() {
        return currentWeekType;
    }

    public int getType() {
        return type;
    }
}