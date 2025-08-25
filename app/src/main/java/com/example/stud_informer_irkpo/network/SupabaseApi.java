package com.example.stud_informer_irkpo.network;

import com.example.stud_informer_irkpo.db.Group;
import com.example.stud_informer_irkpo.db.Teacher;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

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
}
