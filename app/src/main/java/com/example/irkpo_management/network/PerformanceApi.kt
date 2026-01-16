package com.example.irkpo_management.network

import com.example.irkpo_management.models.PerformanceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PerformanceApi {
    @GET("student/{phoneNumber}")
    fun getPerformance(@Path("phoneNumber") phoneNumber: String?): Call<PerformanceResponse?>?
}
