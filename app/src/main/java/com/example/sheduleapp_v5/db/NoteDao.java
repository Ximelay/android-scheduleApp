package com.example.sheduleapp_v5.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NoteEntity note);

    @Query("SELECT * FROM notes WHERE lessonKey = :lessonKey LIMIT 1")
    NoteEntity getNoteForLesson(String lessonKey);

    @Query("SELECT * FROM notes")
    List<NoteEntity> getAllNotes();

    @Query("DELETE FROM notes")
    void deleteAllNotes();
}
