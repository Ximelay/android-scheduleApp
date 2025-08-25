package com.example.stud_informer_irkpo.network;

import com.example.stud_informer_irkpo.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class ApiClient {
    private static final String IRKPO_BASE_URL = BuildConfig.IRKPO_BASE_URL;
    private static final String SUPABASE_URL = BuildConfig.SUPABASE_URL;

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(IRKPO_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static SupabaseApi getSupabaseApi() {
        return new Retrofit.Builder()
                .baseUrl(SUPABASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SupabaseApi.class);
    }
}
