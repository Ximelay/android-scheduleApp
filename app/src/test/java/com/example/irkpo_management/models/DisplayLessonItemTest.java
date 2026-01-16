package com.example.irkpo_management.models;

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

        assertEquals(DisplayLessonItem.TYPE_LESSON, item.type);
        assertEquals("Понедельник", item.dayOfWeek);
        assertEquals("08:00", item.startTime);
        assertEquals("09:30", item.endTime);
        assertTrue(item.isFirstOfDay);
        assertEquals(1, item.currentWeekType);
        assertTrue(item.isVisible());
        assertEquals("Понедельник", item.dayId);
        assertEquals("", item.note);

        item.setVisible(false);
        assertFalse(item.isVisible());

        item.note = "Тестовая заметка";
        assertEquals("Тестовая заметка", item.note);
    }
}
