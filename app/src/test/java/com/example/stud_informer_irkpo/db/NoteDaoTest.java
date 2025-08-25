package com.example.stud_informer_irkpo.db;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class NoteDaoTest {
    private NoteDatabase db;
    private NoteDao noteDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, NoteDatabase.class)
                .allowMainThreadQueries()
                .build();
        noteDao = db.noteDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testInsertAndGetNoteForLesson() {
        NoteEntity note = new NoteEntity("Math101", "Homework", 1633024800000L);
        noteDao.insert(note);

        NoteEntity retrievedNote = noteDao.getNoteForLesson("Math101");
        assertNotNull("Math101", retrievedNote.lessonKey);
        assertEquals("Homework", retrievedNote.text);
        assertEquals(1633024800000L, retrievedNote.remindAtMillis);
    }

    @Test
    public void testInsertReplaceOnConflict() {
        NoteEntity note1 = new NoteEntity("Math101", "Homework", 1633024800000L);
        note1.Id = 1; // Устанавливаем Id вручную
        NoteEntity note2 = new NoteEntity("Math101", "New Homework", 1633024900000L);
        note2.Id = 1; // Тот же Id, чтобы произошла замена

        noteDao.insert(note1);
        noteDao.insert(note2);

        NoteEntity retrievedNote = noteDao.getNoteForLesson("Math101");

        assertNotNull("Note should not be null", retrievedNote);
        assertEquals("Math101", retrievedNote.lessonKey);
        assertEquals("New Homework", retrievedNote.text);
        assertEquals(1633024900000L, retrievedNote.remindAtMillis);
    }

    @Test
    public void testGetAllNotes() {
        NoteEntity note1 = new NoteEntity("Math101", "Homework", 1633024800000L);
        NoteEntity note2 = new NoteEntity("Physics201", "Lab report", 1633024900000L);

        noteDao.insert(note1);
        noteDao.insert(note2);

        List<NoteEntity> notes = noteDao.getAllNotes();

        assertEquals(2, notes.size());
        assertTrue(notes.stream().anyMatch(note -> note.lessonKey.equals("Math101")));
        assertTrue(notes.stream().anyMatch(note -> note.lessonKey.equals("Physics201")));
    }

    @Test
    public void testDeleteByLessonKey() {
        NoteEntity note1 = new NoteEntity("Math101", "Homework", 1633024800000L);
        NoteEntity note2 = new NoteEntity("Physics201", "Lab report", 1633024900000L);

        noteDao.insert(note1);
        noteDao.insert(note2);

        noteDao.deleteByLessonKey("Math101");

        List<NoteEntity> notes = noteDao.getAllNotes();
        assertEquals(1, notes.size());
        assertEquals("Physics201", notes.get(0).lessonKey);
    }

    @Test
    public void testDeleteAllNotes() {
        NoteEntity note1 = new NoteEntity("Math101", "Homework", 1633024800000L);
        NoteEntity note2 = new NoteEntity("Physics201", "Lab report", 1633024900000L);

        noteDao.insert(note1);
        noteDao.insert(note2);

        noteDao.deleteAllNotes();

        List<NoteEntity> notes = noteDao.getAllNotes();
        assertTrue(notes.isEmpty());
    }

    @Test
    public void testGetNoteForLessonNonExistent() {
        NoteEntity retrievedNote = noteDao.getNoteForLesson("NonExistentKey");
        assertNull(retrievedNote);
    }
}
