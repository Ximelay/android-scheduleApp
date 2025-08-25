package com.example.stud_informer_irkpo.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class LessonItemTest {
    @Test
    public void testGetters() {
        LessonItem item = new LessonItem();
        item.setLessonName("Математика");
        item.setTeacherName("Иванов И.И.");
        item.setClassroom("101");
        item.setComment("Лекция");
        item.setSubgroup("подгр. 1");
        item.setWeekType(1);
        item.setLocation("ЖЕЛ");
        item.setGroupName("И-322");

        assertEquals("Математика", item.getLessonName());
        assertEquals("Иванов И.И.", item.getTeacherName());
        assertEquals("101", item.getClassroom());
        assertEquals("Лекция", item.getComment());
        assertEquals("подгр. 1", item.getSubgroup());
        assertEquals(Integer.valueOf(1), item.getWeekType());
        assertEquals("ЖЕЛ", item.getLocation());
        assertEquals("И-322", item.getGroupName());
    }

    @Test
    public void testNullValues() {
        LessonItem item = new LessonItem();
        assertNull(item.getLessonName());
        assertNull(item.getTeacherName());
        assertNull(item.getClassroom());
        assertNull(item.getComment());
        assertNull(item.getSubgroup());
        assertNull(item.getWeekType());
        assertNull(item.getLocation());
        assertNull(item.getGroupName());
    }
}
