package com.example.irkpo_management.db;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class NoteRepositoryTest {

    @Mock
    private NoteDao noteDao;

    private NoteRepository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new NoteRepository(noteDao); // Используем конструктор для тестов
    }

    @Test
    public void testSaveNote() {
        String key = "Math101";
        String text = "Homework";
        Long remindAt = 1633024800000L;

        repository.saveNote(key, text, remindAt);

        verify(noteDao).insert(argThat(note ->
                note.lessonKey.equals(key) &&
                        note.text.equals(text) &&
                        note.remindAtMillis == remindAt
        ));
    }

    @Test
    public void testLoadNote_existingNote() {
        String key = "Math101";
        NoteEntity note = new NoteEntity(key, "Homework", 1633024800000L);
        when(noteDao.getNoteForLesson(key)).thenReturn(note);

        String text = repository.loadNote(key);

        assertEquals("Homework", text);
        verify(noteDao).getNoteForLesson(key);
    }

    @Test
    public void testLoadNote_nonExistentNote() {
        String key = "Math101";
        when(noteDao.getNoteForLesson(key)).thenReturn(null);

        String text = repository.loadNote(key);

        assertEquals("", text);
        verify(noteDao).getNoteForLesson(key);
    }

    @Test
    public void testDeleteNote() {
        String key = "Math101";

        repository.deleteNote(key);

        verify(noteDao).deleteByLessonKey(key);
    }

    @Test
    public void testGetNoteEntity() {
        String key = "Math101";
        NoteEntity note = new NoteEntity(key, "Homework", 1633024800000L);
        when(noteDao.getNoteForLesson(key)).thenReturn(note);

        NoteEntity retrievedNote = repository.getNoteEntity(key);

        assertEquals(note, retrievedNote);
        verify(noteDao).getNoteForLesson(key);
    }

    @Test
    public void testGetNoteEntity_nonExistent() {
        String key = "Math101";
        when(noteDao.getNoteForLesson(key)).thenReturn(null);

        NoteEntity retrievedNote = repository.getNoteEntity(key);

        assertNull(retrievedNote);
        verify(noteDao).getNoteForLesson(key);
    }
}