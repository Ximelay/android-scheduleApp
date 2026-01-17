package com.example.irkpo_management.models

class LessonIndex {
    @JvmField
    val lessonStartTime: String? = null
    @JvmField
    val lessonEndTime: String? = null
    @JvmField
    val items: MutableList<LessonItem?>? = null // Список уроков в этом индексе
}
