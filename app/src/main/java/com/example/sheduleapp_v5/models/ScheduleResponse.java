package com.example.sheduleapp_v5.models;

import java.util.List;

public class ScheduleResponse {
    private String currentDate;
    private int currentWeekType;
    private String currentWeekName;
    private List<DaySchedule> items;

    public String getCurrentDate() {
        return currentDate;
    }

    public int getCurrentWeekType() {
        return currentWeekType;
    }

    public String getCurrentWeekName() {
        return currentWeekName;
    }

    public List<DaySchedule> getItems() {
        return items;
    }
}
