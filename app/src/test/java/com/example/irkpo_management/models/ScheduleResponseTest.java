package com.example.irkpo_management.models;

import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ScheduleResponseTest {
    @Test
    public void testGettersAndSetters() {
        ScheduleResponse response = new ScheduleResponse();
        response.currentDate = "2025-05-28";
        response.currentWeekType = 1;
        response.currentWeekName = "01.01.2025-07.01.2025";
        response.items = new ArrayList<>();

        assertEquals("2025-05-28", response.currentDate);
        assertEquals(1, response.currentWeekType);
        assertEquals("01.01.2025-07.01.2025", response.currentWeekName);
        assertNotNull(response.items);
        assertEquals(0, response.items.size());
    }
}
