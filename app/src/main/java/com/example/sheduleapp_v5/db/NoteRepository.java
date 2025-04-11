package com.example.sheduleapp_v5.db;

import android.content.Context;

public class NoteRepository {
    private final NoteDao noteDao;

    public NoteRepository(Context context) {
        noteDao = NoteDatabase.getInstance(context).noteDao();
    }

    public void saveNote(String key, String text) {
        noteDao.insert(new NoteEntity(key, text));
    }

    public String loadNote(String key) {
        NoteEntity note = noteDao.getNoteForLesson(key);
        return note != null ? note.text : "";
    }
}
