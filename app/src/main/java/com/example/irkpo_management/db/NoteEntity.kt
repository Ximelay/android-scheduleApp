package com.example.irkpo_management.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
class NoteEntity(
    @JvmField var lessonKey: String?,
    @JvmField var text: String?,
    remindAtMillis: Long?
) {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var Id: Int = 0

    @JvmField
    var createAt: Long
    @JvmField
    var remindAtMillis: Long

    init {
        this.createAt = System.currentTimeMillis()
        this.remindAtMillis = if (remindAtMillis != null) remindAtMillis else 0L
    }
}
