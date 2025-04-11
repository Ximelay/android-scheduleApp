package com.example.sheduleapp_v5.db;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "notes")
public class NoteEntity {
    @PrimaryKey(autoGenerate = true)
    public int Id;

    public String lessonKey;
    public String text;

    public NoteEntity(String lessonKey, String text) {
        this.lessonKey = lessonKey;
        this.text = text;
    }
}
