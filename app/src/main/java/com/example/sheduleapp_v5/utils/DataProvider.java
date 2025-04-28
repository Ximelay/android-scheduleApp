package com.example.sheduleapp_v5.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Iterator;

public class DataProvider {
    private static final String TAG = "DataProvider";

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
