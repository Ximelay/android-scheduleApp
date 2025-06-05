package com.example.sheduleapp_v5.db;

import org.junit.Test;
import static org.junit.Assert.*;

public class NoteEntityTest {

    @Test
    public void testNoteEntityConstructor() {
        String lessonKey = "Math101";
        String text = "Homework";
        Long remindAtMillis = 1633024800000L;

        NoteEntity note = new NoteEntity(lessonKey, text, remindAtMillis);

        assertEquals(lessonKey, note.lessonKey);
        assertEquals(text, note.text);
        assertTrue(note.createAt > 0);
        assertEquals(remindAtMillis.longValue(), note.remindAtMillis);
    }

    @Test
    public void textNoteEntityConstructorWithNullRemindAtMillis() {
        String lessonLey = "Math101";
        String text = "Homework";

        NoteEntity note = new NoteEntity(lessonLey, text, null);

        assertEquals(lessonLey, note.lessonKey);
        assertEquals(text, note.text);
        assertTrue(note.createAt > 0);
        assertEquals(0L, note.remindAtMillis);
    }

    @Test
    public void testNoteEntityConstructorWithNullRemindAtMillis() {
        String lessonKey = "Math101";
        String text = "Homework";

        NoteEntity note = new NoteEntity(lessonKey, text, null);

        assertEquals(lessonKey, note.lessonKey);
        assertEquals(text, note.text);
        assertTrue(note.createAt > 0);
        assertEquals(0L, note.remindAtMillis);
    }

    @Test
    public void testNoteEntityConstructorWithNullFields() {
        NoteEntity note = new NoteEntity(null, null, null);
        assertNull(note.lessonKey);
        assertNull(note.text);
        assertTrue(note.createAt > 0);
        assertEquals(0L, note.remindAtMillis);
    }

    @Test
    public void testSettersAndGetters() {
        NoteEntity note = new NoteEntity("Math101", "Homework", 1633024800000L);

        note.setLessonKey("Physic201");
        note.setText("Lab report");
        note.setCreateAt(1234567890L);
        note.setRemindAtMillis(9876543210L);

        assertEquals("Physic201", note.getLessonKey());
        assertEquals("Lab report", note.getText());
        assertEquals(1234567890L, note.getCreateAt());
        assertEquals(9876543210L, note.getRemindAtMillis());
    }
}