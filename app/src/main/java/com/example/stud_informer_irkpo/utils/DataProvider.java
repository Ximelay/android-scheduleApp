package com.example.stud_informer_irkpo.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.stud_informer_irkpo.BuildConfig;
import com.example.stud_informer_irkpo.db.Group;
import com.example.stud_informer_irkpo.db.Teacher;
import com.example.stud_informer_irkpo.network.ApiClient;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataProvider {
    private static final String TAG = "DataProvider";
    private static final String SUPABASE_KEY = BuildConfig.SUPABASE_ANON_KEY;

    public interface LoadCallback<T> {
        void onSuccess(Map<String, T> map);
        void onFailure(Throwable t);
    }

    public static void loadGroupsAsync(Context context, LoadCallback<Integer> callback) {
        String authHeader = "Bearer " + SUPABASE_KEY;
        ApiClient.getSupabaseApi().getGroups(SUPABASE_KEY, authHeader).enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Integer> groupIdMap = new HashMap<>();
                    for (Group group : response.body()) {
                        groupIdMap.put(group.name_group, group.id_group);
                    }
                    Log.d(TAG, "Successfully loaded " + groupIdMap.size() + " groups from Supabase");
                    callback.onSuccess(groupIdMap);
                } else {
                    Log.e(TAG, "Failed to load groups from Supabase. Response code: " + response.code());
                    Map<String, Integer> localMap = loadGroups(context);
                    if (!localMap.isEmpty()) {
                        Log.w(TAG, "Using local groups as fallback");
                        callback.onSuccess(localMap);
                    } else {
                        callback.onFailure(new Exception("Response Error: " + response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Log.e(TAG, "Network error loading groups from Supabase", t);
                Map<String, Integer> localMap = loadGroups(context);
                if (!localMap.isEmpty()) {
                    Log.w(TAG, "Using local groups due to network error");
                    callback.onSuccess(localMap);
                } else {
                    callback.onFailure(t);
                }
            }
        });
    }

    public static void loadTeachersAsync(Context context, LoadCallback<String> callback) {
        String authHeader = "Bearer " + SUPABASE_KEY;
        ApiClient.getSupabaseApi().getTeachers(SUPABASE_KEY, authHeader).enqueue(new Callback<List<Teacher>>() {
            @Override
            public void onResponse(Call<List<Teacher>> call, Response<List<Teacher>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> teacherIdMap = new HashMap<>();
                    for (Teacher teacher : response.body()) {
                        teacherIdMap.put(teacher.name, teacher.id_teacher);
                    }
                    Log.d(TAG, "Successfully loaded " + teacherIdMap.size() + " teachers from Supabase");
                    callback.onSuccess(teacherIdMap);
                } else {
                    Log.e(TAG, "Failed to load teachers from Supabase. Response code: " + response.code());
                    Map<String, String> localMap = loadTeachers(context);
                    if (!localMap.isEmpty()) {
                        Log.w(TAG, "Using local teachers as fallback");
                        callback.onSuccess(localMap);
                    } else {
                        callback.onFailure(new Exception("Response error: " + response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Teacher>> call, Throwable t) {
                Log.e(TAG, "Network error loading teachers from Supabase", t);
                Map<String, String> localMap = loadTeachers(context);
                if (!localMap.isEmpty()) {
                    Log.w(TAG, "Using local teachers due to network error");
                    callback.onSuccess(localMap);
                } else {
                    callback.onFailure(t);
                }
            }
        });
    }

    public static Map<String, Integer> loadGroups(Context context) {
        Map<String, Integer> groupIdMap = new HashMap<>();
        try {
            String jsonString = loadJSONFromAsset(context, "groups.json");
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                groupIdMap.put(key, jsonObject.getInt(key));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading groups", e);
        }
        return groupIdMap;
    }

    public static Map<String, String> loadTeachers(Context context) {
        Map<String, String> teacherIdMap = new HashMap<>();
        try {
            String jsonString = loadJSONFromAsset(context, "teachers.json");
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                teacherIdMap.put(key, jsonObject.getString(key));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading Teachers", e);
        }
        return teacherIdMap;
    }

    private static String loadJSONFromAsset(Context context, String filename) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(filename);
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (Exception e) {
            Log.e(TAG, "Error reading asset file", e);
            return "";
        }
    }
}
