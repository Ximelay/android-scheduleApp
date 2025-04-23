package com.example.sheduleapp_v5.network;

import com.example.sheduleapp_v5.models.PerformanceResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PerformanceApi {
    @GET("student/{phoneNumber}")
    Call<PerformanceResponse> getPerformance(@Path("phoneNumber") String phoneNumber);
}
