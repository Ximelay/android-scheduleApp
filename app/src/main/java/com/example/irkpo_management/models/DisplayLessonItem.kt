package com.example.irkpo_management.models

import android.text.SpannableString

class DisplayLessonItem(
    @JvmField val type: Int,
    @JvmField val dayOfWeek: String?,
    @JvmField val startTime: String?,
    @JvmField val endTime: String?,
    @JvmField val lessons: MutableList<LessonItem?>?,
    @JvmField val isFirstOfDay: Boolean,
    @JvmField val currentWeekType: Int
) {
    var isVisible: Boolean = true
    @JvmField
    val dayId: String?
    @JvmField
    var note: String? = ""
    @JvmField
    var cachedDetails: SpannableString? = null


    init {
        this.dayId = dayOfWeek
    }

    companion object {
        const val TYPE_HEADER: Int = 0
        const val TYPE_LESSON: Int = 1
    }
}
