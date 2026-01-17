package com.example.irkpo_management.db

import android.content.Context

class NoteRepository {
    private val noteDao: NoteDao

    constructor(context: Context?) {
        noteDao = NoteDatabase.getInstance(context).noteDao()
    }

    // Конструктор для тестов
    internal constructor(noteDao: NoteDao) {
        this.noteDao = noteDao
    }

    fun saveNote(key: String?, text: String?, remindAt: Long?) {
        noteDao.insert(NoteEntity(key, text, remindAt))
    }

    fun loadNote(key: String?): String? {
        val note = noteDao.getNoteForLesson(key)
        return if (note != null) note.text else ""
    }

    fun deleteNote(key: String?) {
        noteDao.deleteByLessonKey(key)
    }

    fun getNoteEntity(key: String?): NoteEntity? {
        return noteDao.getNoteForLesson(key)
    }
}
