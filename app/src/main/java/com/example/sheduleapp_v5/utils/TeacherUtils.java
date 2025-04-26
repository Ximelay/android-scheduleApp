package com.example.sheduleapp_v5.utils;

import android.content.Context;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class TeacherUtils {
    private static Map<String, String> teacherIdMap;

    public static void init(Context context) {
        teacherIdMap = DataProvider.loadTeachers(context);
    }

    public static String getTeacherId(String lastName) {
        return teacherIdMap.get(lastName);
    }

    public static Map<String, String> getAllTeachers() {
        return teacherIdMap;
    }

    public static List<String> getFilteredTeachers(String query) {
        List<String> filteredTeachers = new ArrayList<>();
        for (String lastName : teacherIdMap.keySet()) {
            if (lastName.toLowerCase().contains(query.toLowerCase())) {
                filteredTeachers.add(lastName);
            }
        }
        return filteredTeachers;
    }
}
