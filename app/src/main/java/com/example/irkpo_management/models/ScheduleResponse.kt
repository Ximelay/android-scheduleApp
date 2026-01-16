package com.example.irkpo_management.models

class ScheduleResponse {
    @JvmField
    var currentDate: String? = null
    @JvmField
    var currentWeekType: Int = 0
    @JvmField
    var currentWeekName: String? = null
    @JvmField
    var items: MutableList<DaySchedule?>? = null
}
