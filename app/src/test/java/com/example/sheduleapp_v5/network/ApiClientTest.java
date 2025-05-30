package com.example.sheduleapp_v5.network;

import org.junit.Test;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ApiClientTest {

    @Test
    public void testGetRetrofitInstance_sameInstance() {
        Retrofit retrofit1 = ApiClient.getRetrofitInstance();
        Retrofit retrofit2 = ApiClient.getRetrofitInstance();

        assertNotNull(retrofit1);
        assertSame(retrofit1, retrofit2); // Проверяем, что возвращается один и тот же экземпляр
    }

    @Test
    public void testGetRetrofitInstance_correctBaseUrl() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        assertEquals("https://irkpo.ru/mtr/api/", retrofit.baseUrl().toString());
    }

    @Test
    public void testGetRetrofitInstance_hasGsonConverter() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        boolean hasGsonConverter = retrofit.converterFactories().stream()
                .anyMatch(factory -> factory instanceof GsonConverterFactory);
        assertTrue(hasGsonConverter);
    }
}