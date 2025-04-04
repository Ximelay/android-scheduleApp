package com.example.sheduleapp_v5.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupUtils {
    private static final Map<String, Integer> groupIdMap = new HashMap<>();

    // Сюда вводим группу и её уникальный номер
    static {
        groupIdMap.put("И-322", 732);
        groupIdMap.put("И-223", 743);
        groupIdMap.put("И-124", 754);
    }

    public static Integer getGroupId(String groupName) {
        return groupIdMap.get(groupName);
    }

    public static Map<String, Integer> getAllGroups() {
        return groupIdMap;
    }

    // Метод для фильтрации и сортировки групп
    public static List<String> getFilteredGroups(String query) {
        List<String> filteredGroups = new ArrayList<>();
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

        return filteredGroups;
    }

    // Метод для извлечения курса из названия группы (например, "И-322" -> курс 3)
    private static int extractCourse(String groupName) {
        String[] parts = groupName.split("-");
        if (parts.length > 1) {
            try {
                String coursePart = parts[0]; // "И"
                return Integer.parseInt(coursePart.substring(1)); // Извлекаем курс из первой буквы, например, "И" -> 3 для "И-322"
            } catch (NumberFormatException e) {
                return 0; // Если не удается распарсить курс, возвращаем 0
            }
        }
        return 0;
    }

    // Метод для извлечения числовой части из названия группы
    private static int extractNumber(String groupName) {
        // Разделяем строку по символу "-" и парсим вторую часть как число
        String[] parts = groupName.split("-");
        if (parts.length > 1) {
            try {
                return Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                return 0; // Если не удается распарсить число, возвращаем 0
            }
        }
        return 0;
    }
}
