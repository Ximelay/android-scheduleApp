package com.example.stud_informer_irkpo.models;

import java.util.List;

public class ScheduleResponse {
    private String currentDate;
    private int currentWeekType;
    private String currentWeekName;
    private List<DaySchedule> items;

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public int getCurrentWeekType() {
        return currentWeekType;
    }

    public void setCurrentWeekType(int currentWeekType) {
        this.currentWeekType = currentWeekType;
    }

    public String getCurrentWeekName() {
        return currentWeekName;
    }

    public void setCurrentWeekName(String currentWeekName) {
        this.currentWeekName = currentWeekName;
    }

    public List<DaySchedule> getItems() {
        return items;
    }

    public void setItems(List<DaySchedule> items) {
        this.items = items;
    }
}
