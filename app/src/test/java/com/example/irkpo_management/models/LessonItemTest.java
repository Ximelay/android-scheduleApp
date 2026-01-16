package com.example.irkpo_management.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class LessonItemTest {
    @Test
    public void testGetters() {
        LessonItem item = new LessonItem();
        item.lessonName = "Математика";
        item.teacherName = "Иванов И.И.";
        item.classroom = "101";
        item.comment = "Лекция";
        item.subgroup = "подгр. 1";
        item.weekType = 1;
        item.location = "ЖЕЛ";
        item.groupName = "И-322";

        assertEquals("Математика", item.lessonName);
        assertEquals("Иванов И.И.", item.teacherName);
        assertEquals("101", item.classroom);
        assertEquals("Лекция", item.comment);
        assertEquals("подгр. 1", item.subgroup);
        assertEquals(Integer.valueOf(1), item.weekType);
        assertEquals("ЖЕЛ", item.location);
        assertEquals("И-322", item.groupName);
    }

    @Test
    public void testNullValues() {
        LessonItem item = new LessonItem();
        assertNull(item.lessonName);
        assertNull(item.teacherName);
        assertNull(item.classroom);
        assertNull(item.comment);
        assertNull(item.subgroup);
        assertNull(item.weekType);
        assertNull(item.location);
        assertNull(item.groupName);
    }
}
