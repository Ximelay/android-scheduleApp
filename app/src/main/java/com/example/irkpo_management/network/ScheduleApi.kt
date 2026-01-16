package com.example.irkpo_management.network;

import com.example.irkpo_management.models.ScheduleResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface ScheduleApi {
    @GET("schedule")
    Call<ScheduleResponse> getSchedule(@Query("GroupId") int groupId);

    @GET("schedule")
    Call<ScheduleResponse> getScheduleByPersonId(@Query("PersonId") String personId);
}
