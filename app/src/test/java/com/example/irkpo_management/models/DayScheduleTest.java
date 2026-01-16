package com.example.irkpo_management.models;

import static org.junit.Assert.*;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class DayScheduleTest {
    @Test
    public void testDayScheduleGetters() throws NoSuchFieldException, IllegalAccessException {
        DaySchedule daySchedule = new DaySchedule();

        Field dayOfWeekField = DaySchedule.class.getDeclaredField("dayOfWeek");
        dayOfWeekField.setAccessible(true);
        dayOfWeekField.set(daySchedule, "Monday");

        Field weekTypeField = DaySchedule.class.getDeclaredField("weekType");
        weekTypeField.setAccessible(true);
        weekTypeField.set(daySchedule, 1);

        Field lessonIndexesField = DaySchedule.class.getDeclaredField("lessonIndexes");
        lessonIndexesField.setAccessible(true);
        List<LessonIndex> lessonIndices = Arrays.asList(new LessonIndex());
        lessonIndexesField.set(daySchedule, lessonIndices);

        assertEquals("Monday", daySchedule.dayOfWeek);
        assertEquals(1, daySchedule.weekType);
        assertEquals(lessonIndices, daySchedule.lessonIndexes);
    }

    @Test
    public void testDayScheduleWithNullValues() throws NoSuchFieldException, IllegalAccessException {
        DaySchedule daySchedule = new DaySchedule();

        Field dayOdWeekField = DaySchedule.class.getDeclaredField("dayOfWeek");
        dayOdWeekField.setAccessible(true);
        dayOdWeekField.set(daySchedule, null);

        Field lessonIndexesField = DaySchedule.class.getDeclaredField("lessonIndexes");
        lessonIndexesField.setAccessible(true);
        lessonIndexesField.set(daySchedule, null);

        assertNull(daySchedule.dayOfWeek);
        assertEquals(0, daySchedule.weekType);
        assertNull(daySchedule.lessonIndexes);
    }
}
