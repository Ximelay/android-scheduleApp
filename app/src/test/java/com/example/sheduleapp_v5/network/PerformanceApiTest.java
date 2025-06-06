package com.example.sheduleapp_v5.network;

import com.example.sheduleapp_v5.models.PerformanceResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import retrofit2.Call;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PerformanceApiTest {

    @Mock
    private PerformanceApi performanceApi;

    @Mock
    private Call<PerformanceResponse> mockCall;

    @Before
    public void setUp() {
        when(performanceApi.getPerformance("1234567890")).thenReturn(mockCall);
    }

    @Test
    public void testGetPerformance_withPhoneNumber() {
        String phoneNumber = "1234567890";
        performanceApi.getPerformance(phoneNumber);
        verify(performanceApi).getPerformance(phoneNumber);
    }
}