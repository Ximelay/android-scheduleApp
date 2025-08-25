package com.example.stud_informer_irkpo.network;

import com.example.stud_informer_irkpo.models.ScheduleResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import retrofit2.Call;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleApiTest {

    @Mock
    private ScheduleApi scheduleApi;

    @Mock
    private Call<ScheduleResponse> mockCall;

    @Before
    public void setUp() {
        // Настройка моков
        when(scheduleApi.getSchedule(123)).thenReturn(mockCall);
        when(scheduleApi.getScheduleByPersonId("456")).thenReturn(mockCall);
    }

    @Test
    public void testGetSchedule_withGroupId() {
        int groupId = 123;
        scheduleApi.getSchedule(groupId);
        verify(scheduleApi).getSchedule(groupId);
    }

    @Test
    public void testGetScheduleByPersonId() {
        String personId = "456";
        scheduleApi.getScheduleByPersonId(personId);
        verify(scheduleApi).getScheduleByPersonId(personId);
    }
}