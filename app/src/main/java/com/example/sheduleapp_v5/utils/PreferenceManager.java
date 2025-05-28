package com.example.sheduleapp_v5.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "schedule_preferences";
    private static final String KEY_DEFAULT_GROUP = "default_group";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_GROUP_ID = "group_id";
    private static final String SCHEDULE_CACHE_KEY = "cached_schedule";
    private static final String CACHED_GROUP_ID = "cached_group_id";
    private static final String PERFORMANCE_CACHE_KEY = "performance_cache";
    private static final String LAST_CACHE_TIME_KEY = "last_cache_time";

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

    public String getPhoneNumber() {
        return sharedPreferences.getString(KEY_PHONE_NUMBER, null);
    }

    public void setPhoneNumber(String phoneNumber) {
        sharedPreferences.edit().putString(KEY_PHONE_NUMBER, phoneNumber).apply();
    }

    public int getGroupId() {
        return sharedPreferences.getInt(KEY_GROUP_ID, -1);
    }

    public void setGroupId(int groupId) {
        sharedPreferences.edit().putInt(KEY_GROUP_ID, groupId).apply();
    }

    public int getCachedGroupId() {
        return sharedPreferences.getInt(CACHED_GROUP_ID, -1);
    }

    public void setCachedGroupId(int groupId) {
        sharedPreferences.edit().putInt(CACHED_GROUP_ID, groupId).apply();
    }

    public String getScheduleCache() {
        return sharedPreferences.getString(SCHEDULE_CACHE_KEY, "");
    }

    public void setScheduleCache(String scheduleJson) {
        sharedPreferences.edit().putString(SCHEDULE_CACHE_KEY, scheduleJson).apply();
    }

    public void setPerformanceCache(String json) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PERFORMANCE_CACHE_KEY, json);
        editor.apply();
    }

    public String getPerformanceCache() {
        return sharedPreferences.getString(PERFORMANCE_CACHE_KEY, null);
    }

    public void setLastCacheTime(long time) {
        sharedPreferences.edit().putLong(LAST_CACHE_TIME_KEY, time).apply();
    }

    public long getLastCacheTime() {
        return sharedPreferences.getLong(LAST_CACHE_TIME_KEY, 0);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}