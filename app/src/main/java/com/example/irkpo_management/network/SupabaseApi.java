package com.example.irkpo_management.network;

import com.example.irkpo_management.db.CreateUserConsentRequest;
import com.example.irkpo_management.db.Group;
import com.example.irkpo_management.db.Teacher;
import com.example.irkpo_management.db.UserConsent;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {
    @GET("rest/v1/groups?select=*")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    Call<List<Group>> getGroups(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );
    
    @GET("rest/v1/teachers?select=*")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    Call<List<Teacher>> getTeachers(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );

    /**
     * Создание нового согласия пользователя на обработку персональных данных
     */
    @POST("rest/v1/user_consents")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    Call<List<UserConsent>> createUserConsent(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization,
            @Body CreateUserConsentRequest consent
    );

    /**
     * Получение согласия пользователя по user_id
     */
    @GET("rest/v1/user_consents")
    @Headers({
            "Content-Type: application/json"
    })
    Call<List<UserConsent>> getUserConsent(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization,
            @Query("user_id") String userId,
            @Query("select") String select
    );
}
