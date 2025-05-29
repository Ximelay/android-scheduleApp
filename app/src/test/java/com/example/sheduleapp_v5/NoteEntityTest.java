package com.example.sheduleapp_v5;

import com.example.sheduleapp_v5.db.NoteEntity;

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
}