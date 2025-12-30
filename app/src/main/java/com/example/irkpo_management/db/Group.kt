package com.example.irkpo_management.db

import com.google.gson.annotations.SerializedName

class Group {
    var name_group: String? = null
    @SerializedName("group_id")
    var id_group: Int = 0
}
