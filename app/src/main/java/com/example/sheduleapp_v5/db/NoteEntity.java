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

    public String getLessonKey() {
        return lessonKey;
    }

    public void setLessonKey(String lessonKey) {
        this.lessonKey = lessonKey;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getRemindAtMillis() {
        return remindAtMillis;
    }

    public void setRemindAtMillis(long remindAtMillis) {
        this.remindAtMillis = remindAtMillis;
    }
}
