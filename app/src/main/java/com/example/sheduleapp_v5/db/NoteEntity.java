package com.example.sheduleapp_v5.db;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "notes")
public class NoteEntity {
    @PrimaryKey(autoGenerate = true)
    public int Id;

    public String lessonKey;
    public String text;
    public long createAt;
    public long remindAtMillis;

    public NoteEntity(String lessonKey, String text, Long remindAtMillis) {
        this.lessonKey = lessonKey;
        this.text = text;
        this.createAt = System.currentTimeMillis();
        this.remindAtMillis = (remindAtMillis != null) ? remindAtMillis : 0L;
    }
}
