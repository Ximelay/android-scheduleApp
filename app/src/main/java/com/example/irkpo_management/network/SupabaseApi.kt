package com.example.irkpo_management.network

import com.example.irkpo_management.db.CreateUserConsentRequest
import com.example.irkpo_management.db.Group
import com.example.irkpo_management.db.Teacher
import com.example.irkpo_management.db.UserConsent
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {
    @GET("rest/v1/groups?select=*")
    @Headers(
        "Content-Type: application/json", "Prefer: return=representation"
    )
    fun getGroups(
        @Header("apikey") apiKey: String?,
        @Header("Authorization") authorization: String?
    ): Call<MutableList<Group?>?>?

    @GET("rest/v1/teachers?select=*")
    @Headers(
        "Content-Type: application/json", "Prefer: return=representation"
    )
    fun getTeachers(
        @Header("apikey") apiKey: String?,
        @Header("Authorization") authorization: String?
    ): Call<MutableList<Teacher?>?>?

    /**
     * Создание нового согласия пользователя на обработку персональных данных
     */
    @POST("rest/v1/user_consents")
    @Headers(
        "Content-Type: application/json", "Prefer: return=representation"
    )
    fun createUserConsent(
        @Header("apikey") apiKey: String?,
        @Header("Authorization") authorization: String?,
        @Body consent: CreateUserConsentRequest?
    ): Call<MutableList<UserConsent?>?>?

    /**
     * Получение согласия пользователя по user_id
     */
    @GET("rest/v1/user_consents")
    @Headers(
        "Content-Type: application/json"
    )
    fun getUserConsent(
        @Header("apikey") apiKey: String?,
        @Header("Authorization") authorization: String?,
        @Query("user_id") userId: String?,
        @Query("select") select: String?
    ): Call<MutableList<UserConsent?>?>?
}
