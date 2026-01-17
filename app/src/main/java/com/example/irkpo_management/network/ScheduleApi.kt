package com.example.irkpo_management.network

import com.example.irkpo_management.models.ScheduleResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ScheduleApi {
    @GET("schedule")
    fun getSchedule(@Query("GroupId") groupId: Int): Call<ScheduleResponse?>?

    @GET("schedule")
    fun getScheduleByPersonId(@Query("PersonId") personId: String?): Call<ScheduleResponse?>?
}
