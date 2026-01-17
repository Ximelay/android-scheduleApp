package com.example.irkpo_management.models;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class LessonIndexTest {
    @Test
    public void testLessonIndexGetters() throws NoSuchFieldException, IllegalAccessException {
        LessonIndex lessonIndex = new LessonIndex();

        // Устанавливаем значения через рефлексию, так как нет сеттеров
        Field startTimeField = LessonIndex.class.getDeclaredField("lessonStartTime");
        startTimeField.setAccessible(true);
        startTimeField.set(lessonIndex, "09:00");

        Field endTimeField = LessonIndex.class.getDeclaredField("lessonEndTime");
        endTimeField.setAccessible(true);
        endTimeField.set(lessonIndex, "10:30");

        Field itemsField = LessonIndex.class.getDeclaredField("items");
        itemsField.setAccessible(true);
        List<LessonItem> items = Arrays.asList(new LessonItem());
        itemsField.set(lessonIndex, items);

        assertEquals("09:00", lessonIndex.lessonStartTime);
        assertEquals("10:30", lessonIndex.lessonEndTime);
        assertEquals(items, lessonIndex.items);
    }

    @Test
    public void testLessonIndexWithNullValues() throws NoSuchFieldException, IllegalAccessException {
        LessonIndex lessonIndex = new LessonIndex();

        Field startTimeField = LessonIndex.class.getDeclaredField("lessonStartTime");
        startTimeField.setAccessible(true);
        startTimeField.set(lessonIndex, null);

        Field endTimeField = LessonIndex.class.getDeclaredField("lessonEndTime");
        endTimeField.setAccessible(true);
        endTimeField.set(lessonIndex, null);

        assertNull(lessonIndex.lessonStartTime);
        assertNull(lessonIndex.lessonEndTime);
        assertNull(lessonIndex.items);
    }
}
