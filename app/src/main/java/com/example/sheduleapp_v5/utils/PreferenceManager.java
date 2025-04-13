package com.example.sheduleapp_v5.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "schedule_preferences";
    private static final String KEY_DEFAULT_GROUP = "default_group";

    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setDefaultGroup(String groupName) {
        sharedPreferences.edit().putString(KEY_DEFAULT_GROUP, groupName).apply();
    }

    public String getDefaultGroup() {
        return sharedPreferences.getString(KEY_DEFAULT_GROUP, null);
    }
}