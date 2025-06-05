package com.example.sheduleapp_v5.models;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DisplayLessonItemTest {
    @Test
    public void testGettersAndSetters() {
        DisplayLessonItem item = new DisplayLessonItem(
                DisplayLessonItem.TYPE_LESSON,
                "Понедельник",
                "08:00",
                "09:30",
                new ArrayList<>(),
                true,
                1
        );

        assertEquals(DisplayLessonItem.TYPE_LESSON, item.getType());
        assertEquals("Понедельник", item.getDayOfWeek());
        assertEquals("08:00", item.getStartTime());
        assertEquals("09:30", item.getEndTime());
        assertTrue(item.isFirstOfDay());
        assertEquals(1, item.getCurrentWeekType());
        assertTrue(item.isVisible());
        assertEquals("Понедельник", item.getDayId());
        assertEquals("", item.getNote());

        item.setVisible(false);
        assertFalse(item.isVisible());

        item.setNote("Тестовая заметка");
        assertEquals("Тестовая заметка", item.getNote());
    }
}
