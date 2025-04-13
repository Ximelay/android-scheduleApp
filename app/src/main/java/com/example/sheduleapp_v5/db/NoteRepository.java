package com.example.sheduleapp_v5.db;

import android.content.Context;

public class NoteRepository {
    private final NoteDao noteDao;

    public NoteRepository(Context context) {
        noteDao = NoteDatabase.getInstance(context).noteDao();
    }

    public void saveNote(String key, String text, Long remindAt) {
        noteDao.insert(new NoteEntity(key, text, remindAt));
    }

    public String loadNote(String key) {
        NoteEntity note = noteDao.getNoteForLesson(key);
        return note != null ? note.text : "";
    }

    public void deleteNote(String key) {
        noteDao.deleteByLessonKey(key);
    }

    public NoteEntity getNoteEntity(String key) {
        return noteDao.getNoteForLesson(key);
    }
}
