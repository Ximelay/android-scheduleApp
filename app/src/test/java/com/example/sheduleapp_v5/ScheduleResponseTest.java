package com.example.sheduleapp_v5;

import com.example.sheduleapp_v5.models.ScheduleResponse;

import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ScheduleResponseTest {
    @Test
    public void testGettersAndSetters() {
        ScheduleResponse response = new ScheduleResponse();
        response.setCurrentDate("2025-05-28");
        response.setCurrentWeekType(1);
        response.setCurrentWeekName("01.01.2025-07.01.2025");
        response.setItems(new ArrayList<>());

        assertEquals("2025-05-28", response.getCurrentDate());
        assertEquals(1, response.getCurrentWeekType());
        assertEquals("01.01.2025-07.01.2025", response.getCurrentWeekName());
        assertNotNull(response.getItems());
        assertEquals(0, response.getItems().size());
    }
}
