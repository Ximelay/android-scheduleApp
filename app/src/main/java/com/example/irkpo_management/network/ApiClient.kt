package com.example.irkpo_management.network

import com.example.irkpo_management.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val IRKPO_BASE_URL = BuildConfig.IRKPO_BASE_URL
    private val SUPABASE_URL = BuildConfig.SUPABASE_URL

    private var retrofit: Retrofit? = null

    @JvmStatic
    val retrofitInstance: Retrofit
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(IRKPO_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }

    @JvmStatic
    val supabaseApi: SupabaseApi
        get() = Retrofit.Builder()
            .baseUrl(SUPABASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<SupabaseApi>(SupabaseApi::class.java)
}
