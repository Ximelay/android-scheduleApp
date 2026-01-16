package com.example.irkpo_management.models

class DaySchedule {
    // Геттеры
    @JvmField
    val dayOfWeek: String? = null
    @JvmField
    val weekType: Int = 0
    @JvmField
    val lessonIndexes: MutableList<LessonIndex?>? = null // Уроки на этот день
}
