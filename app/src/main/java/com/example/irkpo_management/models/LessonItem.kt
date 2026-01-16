package com.example.irkpo_management.models

import com.google.gson.annotations.SerializedName

class LessonItem {
    @JvmField
    @SerializedName("lessonName")
    var lessonName: String? = null

    @JvmField
    @SerializedName("teacherName")
    var teacherName: String? = null

    @JvmField
    @SerializedName("classroom")
    var classroom: String? = null

    @JvmField
    @SerializedName("comment")
    var comment: String? = null

    @JvmField
    @SerializedName("subgroup")
    var subgroup: String? = null

    @JvmField
    @SerializedName("weekType")
    var weekType: Int? = null

    @JvmField
    @SerializedName("location")
    var location: String? = null

    @JvmField
    @SerializedName("groupName")
    var groupName: String? = null
}
