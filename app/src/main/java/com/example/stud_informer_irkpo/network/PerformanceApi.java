package com.example.stud_informer_irkpo.network;

import com.example.stud_informer_irkpo.models.PerformanceResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PerformanceApi {
    @GET("student/{phoneNumber}")
    Call<PerformanceResponse> getPerformance(@Path("phoneNumber") String phoneNumber);
}
