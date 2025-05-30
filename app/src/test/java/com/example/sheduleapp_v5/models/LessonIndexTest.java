package com.example.sheduleapp_v5.models;

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

        assertEquals("09:00", lessonIndex.getLessonStartTime());
        assertEquals("10:30", lessonIndex.getLessonEndTime());
        assertEquals(items, lessonIndex.getItems());
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

        assertNull(lessonIndex.getLessonStartTime());
        assertNull(lessonIndex.getLessonEndTime());
        assertNull(lessonIndex.getItems());
    }
}
