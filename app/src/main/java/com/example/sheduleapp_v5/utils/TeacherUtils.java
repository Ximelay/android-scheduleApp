package com.example.sheduleapp_v5.utils;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class TeacherUtils {
    private static final String TAG = "TeacherUtils";
    private static Map<String, String> teacherIdMap;
    private static final int FUZZY_THRESHOLD = 80; // Порог для FuzzyWuzzy (80% совпадения)

    public static void init(Context context) {
        teacherIdMap = DataProvider.loadTeachers(context);
        Log.d(TAG, "Initialized with " + (teacherIdMap != null ? teacherIdMap.size() : 0) + " teachers");
    }

    public static String getTeacherId(String lastName) {
        return teacherIdMap != null ? teacherIdMap.get(lastName) : null;
    }

    public static Map<String, String> getAllTeachers() {
        return teacherIdMap != null ? teacherIdMap : Collections.emptyMap();
    }

    public static List<String> getFilteredTeachers(String query) {
        List<String> filteredTeachers = new ArrayList<>();
        if (teacherIdMap == null || query == null || query.trim().isEmpty()) {
            Log.w(TAG, "Cannot filter teachers: teacherIdMap=" + (teacherIdMap == null ? "null" : teacherIdMap.size()) + ", query=" + query);
            return filteredTeachers;
        }

        String queryLower = query.trim().toLowerCase();
        Log.d(TAG, "Filtering teachers for query: '" + query + "'");

        // Списки для преподавателей по фамилии, имени, отчеству
        List<String> byLastName = new ArrayList<>();
        List<String> byFirstName = new ArrayList<>();
        List<String> byPatronymic = new ArrayList<>();

        for (String fullName : teacherIdMap.keySet()) {
            if (fullName == null || fullName.trim().isEmpty()) {
                Log.w(TAG, "Skipping invalid fullName: " + fullName);
                continue;
            }

            String[] parts = fullName.trim().split("\\s+");
            Log.d(TAG, "Processing fullName: '" + fullName + "', parts: " + parts.length);

            if (parts.length >= 3) {
                String lastName = parts[0];
                String firstName = parts[1];
                String patronymic = parts[2];
                String formattedName = formatTeacherName(fullName);

                // Проверяем подстроку или FuzzyWuzzy
                if (lastName.toLowerCase().contains(queryLower) ||
                        FuzzySearch.ratio(lastName.toLowerCase(), queryLower) >= FUZZY_THRESHOLD) {
                    byLastName.add(formattedName);
                    Log.d(TAG, "Matched by lastName: " + formattedName + " (original: " + fullName + ")");
                } else if (firstName.toLowerCase().contains(queryLower) ||
                        FuzzySearch.ratio(firstName.toLowerCase(), queryLower) >= FUZZY_THRESHOLD) {
                    byFirstName.add(formattedName);
                    Log.d(TAG, "Matched by firstName: " + formattedName + " (original: " + fullName + ")");
                } else if (patronymic.toLowerCase().contains(queryLower) ||
                        FuzzySearch.ratio(patronymic.toLowerCase(), queryLower) >= FUZZY_THRESHOLD) {
                    byPatronymic.add(formattedName);
                    Log.d(TAG, "Matched by patronymic: " + formattedName + " (original: " + fullName + ")");
                }
            } else {
                Log.w(TAG, "Non-standard fullName format: " + fullName + ", skipping");
            }
        }

        // Сортируем каждый список по алфавиту
        Collections.sort(byLastName);
        Collections.sort(byFirstName);
        Collections.sort(byPatronymic);

        // Объединяем: по фамилии → по имени → по отчеству
        filteredTeachers.addAll(byLastName);
        filteredTeachers.addAll(byFirstName);
        filteredTeachers.addAll(byPatronymic);

        Log.d(TAG, "Filtered " + filteredTeachers.size() + " teachers for query '" + query + "': " +
                "byLastName=" + byLastName.size() + ", byFirstName=" + byFirstName.size() + ", byPatronymic=" + byPatronymic.size() + ", total=" + filteredTeachers);
        return filteredTeachers;
    }

    private static String formatTeacherName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length < 3) {
            Log.w(TAG, "Non-standard fullName format: " + fullName + ", returning as is");
            return fullName;
        }
        String lastName = parts[0];
        String firstNameInitial = parts[1].substring(0, 1) + ".";
        String patronymicInitial = parts[2].substring(0, 1) + ".";
        return lastName + " " + firstNameInitial + patronymicInitial;
    }
}