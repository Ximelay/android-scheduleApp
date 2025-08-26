package com.example.irkpo_management.utils;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GroupUtils {
    private static Map<String, Integer> groupIdMap;

    public static void init(Context context, DataProvider.LoadCallback<Integer> callback) {
        DataProvider.loadGroupsAsync(context, new DataProvider.LoadCallback<Integer>() {
            @Override
            public void onSuccess(Map<String, Integer> map) {
                groupIdMap = map;
                Log.d("GroupUtils", "Initialized groupIdMap with " + map.size() + " groups");
                callback.onSuccess(map);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("GroupUtils", "Failed to load groups", t);
                callback.onFailure(t);
            }
        });
    }

    public static Integer getGroupId(String groupName) {
        if (groupIdMap == null || groupName == null) {
            Log.e("GroupUtils", "groupIdMap is null or groupName is null");
            return null;
        }
        Integer groupId = groupIdMap.get(groupName);
        Log.d("GroupUtils", "getGroupId for " + groupName + ": " + groupId);
        return groupId;
    }

    public static String getGroupName(int groupId) {
        if (groupIdMap == null) {
            Log.e("GroupUtils", "groupIdMap is null");
            return null;
        }
        for (Map.Entry<String, Integer> entry : groupIdMap.entrySet()) {
            if (entry.getValue() == groupId) {
                Log.d("GroupUtils", "getGroupName for groupId " + groupId + ": " + entry.getKey());
                return entry.getKey();
            }
        }
        Log.w("GroupUtils", "No group found for groupId: " + groupId);
        return null;
    }

    public static Map<String, Integer> getAllGroups() {
        return groupIdMap;
    }

    // Метод для фильтрации и сортировки групп
    public static List<String> getFilteredGroups(String query) {
        List<String> filteredGroups = new ArrayList<>();
        if (groupIdMap == null || query == null) {
            Log.e("GroupUtils", "groupIdMap or query is null");
            return filteredGroups;
        }

        for (String groupName : groupIdMap.keySet()) {
            // Убираем пробелы для корректного поиска
            if (groupName.replace(" ", "").toLowerCase().contains(query.replace(" ", "").toLowerCase())) {
                filteredGroups.add(groupName);
            }
        }

        // Сортировка списка групп сначала по курсу (по убыванию), затем по числовому значению в названии
        Collections.sort(filteredGroups, new Comparator<String>() {
            @Override
            public int compare(String group1, String group2) {
                int course1 = extractCourse(group1); // Получаем курс из первой группы
                int course2 = extractCourse(group2); // Получаем курс из второй группы

                // Сортируем сначала по курсу (по убыванию)
                if (course2 != course1) {
                    return Integer.compare(course2, course1);
                }

                // Если курсы одинаковые, сортируем по числовому значению группы
                int num1 = extractNumber(group1);
                int num2 = extractNumber(group2);
                return Integer.compare(num2, num1); // Сортируем от большего к меньшему
            }
        });

        Log.d("GroupUtils", "Filtered groups for query '" + query + "': " + filteredGroups.size());
        return filteredGroups;
    }

    // Метод для извлечения курса из названия группы (например, "И-322" -> курс 3)
    private static int extractCourse(String groupName) {
        if (groupName == null) return 0;
        String[] parts = groupName.split("-");
        if (parts.length > 1) {
            try {
                // Извлекаем первую цифру из номера группы
                return Integer.parseInt(parts[1].substring(0, 1));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                Log.w("GroupUtils", "Failed to extract course from " + groupName + ": " + e.getMessage());
                return 0; // Если не удается распарсить курс, возвращаем 0
            }
        }
        return 0;
    }

    // Метод для извлечения числовой части из названия группы
    private static int extractNumber(String groupName) {
        if (groupName == null) return 0;
        // Разделяем строку по символу "-" и парсим вторую часть как число
        String[] parts = groupName.split("-");
        if (parts.length > 1) {
            try {
                return Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                Log.w("GroupUtils", "Failed to extract number from " + groupName + ": " + e.getMessage());
                return 0; // Если не удается распарсить число, возвращаем 0
            }
        }
        return 0;
    }
}