package com.example.sheduleapp_v5;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class TestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Устанавливаем тему для AppCompat
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setTheme(R.style.Theme_SheduleApp_v5);
    }
}