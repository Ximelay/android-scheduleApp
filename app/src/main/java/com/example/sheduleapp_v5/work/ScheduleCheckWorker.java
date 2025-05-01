package com.example.sheduleapp_v5.work;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sheduleapp_v5.R;
import com.example.sheduleapp_v5.models.DaySchedule;
import com.example.sheduleapp_v5.models.LessonIndex;
import com.example.sheduleapp_v5.models.LessonItem;
import com.example.sheduleapp_v5.models.ScheduleResponse;
import com.example.sheduleapp_v5.network.ApiClient;
import com.example.sheduleapp_v5.network.ScheduleApi;
import com.example.sheduleapp_v5.utils.PreferenceManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class ScheduleCheckWorker extends Worker {
    private static final String CHANNEL_ID = "schedule_check_channel";
    private static final int NOTIFICATION_ID = 1;

    public ScheduleCheckWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("ScheduleCheckWorker", "🔄 Worker started");
        try {
            PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
            int groupId = preferenceManager.getGroupId();

            if (groupId == -1) {
                Log.e("ScheduleCheckWorker", "GroupId not found");
                return Result.failure();
            }
            Log.d("ScheduleCheckWorker", "GroupId: " + groupId);

            ScheduleApi api = ApiClient.getRetrofitInstance().create(ScheduleApi.class);
            Call<ScheduleResponse> call = api.getSchedule(groupId);
            Response<ScheduleResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                Gson gson = new Gson();
                ScheduleResponse newSchedule = response.body();
                String newScheduleJson = gson.toJson(newSchedule);
                Log.d("ScheduleCheckWorker", "New schedule fetched: " + newScheduleJson);

                int cachedGroupId = preferenceManager.getCachedGroupId();
                String oldScheduleJson = preferenceManager.getScheduleCache();
                Log.d("ScheduleCheckWorker", "Old schedule from cache (cachedGroupId: " + cachedGroupId + "): " + oldScheduleJson);

                // Сохраняем новое расписание и groupId в кэш
                if (newScheduleJson != null && !newScheduleJson.equals("{}")) {
                    preferenceManager.setScheduleCache(newScheduleJson);
                    preferenceManager.setCachedGroupId(groupId);
                    Log.d("ScheduleCheckWorker", "Saved new schedule to cache for groupId: " + groupId);
                } else {
                    Log.e("ScheduleCheckWorker", "Invalid new schedule JSON, not saving");
                    return Result.retry();
                }

                // Пропускаем уведомление, если кэш пуст или относится к другой группе
                if (oldScheduleJson.isEmpty() || cachedGroupId != groupId) {
                    Log.d("ScheduleCheckWorker", "First fetch or group changed (cachedGroupId: " + cachedGroupId + ", current: " + groupId + "), skipping notification");
                    return Result.success();
                }

                // Парсим старое расписание
                ScheduleResponse oldSchedule = null;
                try {
                    oldSchedule = gson.fromJson(oldScheduleJson, ScheduleResponse.class);
                    Log.d("ScheduleCheckWorker", "Parsed old schedule: " + oldSchedule);
                } catch (Exception e) {
                    Log.e("ScheduleCheckWorker", "Failed to parse old schedule", e);
                    return Result.success(); // Пропускаем уведомление, если кэш некорректен
                }

                if (oldSchedule == null || oldSchedule.getItems() == null) {
                    Log.d("ScheduleCheckWorker", "Invalid old schedule, skipping notification");
                    return Result.success();
                }

                // Проверяем изменения
                List<String> changedDays = getChangedDays(oldSchedule, newSchedule);
                Log.d("ScheduleCheckWorker", "Changed days: " + changedDays);

                // Отправляем уведомление только если есть измененные дни
                if (!changedDays.isEmpty()) {
                    String groupName = preferenceManager.getDefaultGroup();
                    String days = String.join(", ", changedDays);
                    String message = "У группы " + (groupName != null ? groupName : "неизвестно") +
                            " изменилось расписание на " + days;

                    sendNotification(message);
                } else {
                    Log.d("ScheduleCheckWorker", "No significant changes detected for groupId " + groupId);
                }
            } else {
                Log.e("ScheduleCheckWorker", "API response failed: " + response.code());
                return Result.retry();
            }
            return Result.success();
        } catch (Exception e) {
            Log.e("ScheduleCheckWorker", "Worker failed", e);
            return Result.retry();
        }
    }

    private void sendNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Проверка расписания",
                    NotificationManager.IMPORTANCE_HIGH
            );
            Log.d("ScheduleCheckWorker", "Creating notification channel");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Обновление расписания")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        Log.d("ScheduleCheckWorker", "Sending notification: " + message);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private List<String> getChangedDays(ScheduleResponse oldData, ScheduleResponse newData) {
        List<String> changedDays = new ArrayList<>();

        List<DaySchedule> oldDays = oldData.getItems();
        List<DaySchedule> newDays = newData.getItems();

        // Сравниваем дни по их содержимому
        for (int i = 0; i < Math.min(oldDays.size(), newDays.size()); i++) {
            DaySchedule oldDay = oldDays.get(i);
            DaySchedule newDay = newDays.get(i);

            if (!areDaysEqual(oldDay, newDay)) {
                changedDays.add(newDay.getDayOfWeek());
            }
        }

        // Проверяем, появились ли новые дни в новом расписании
        if (newDays.size() > oldDays.size()) {
            for (int i = oldDays.size(); i < newDays.size(); i++) {
                changedDays.add(newDays.get(i).getDayOfWeek());
            }
        }

        return changedDays;
    }

    private boolean areDaysEqual(DaySchedule oldDay, DaySchedule newDay) {
        if (!Objects.equals(oldDay.getDayOfWeek(), newDay.getDayOfWeek())) {
            Log.d("ScheduleCheckWorker", "Days differ by dayOfWeek: " + oldDay.getDayOfWeek() + " vs " + newDay.getDayOfWeek());
            return false;
        }

        List<LessonIndex> oldLessons = oldDay.getLessonIndexes();
        List<LessonIndex> newLessons = newDay.getLessonIndexes();

        if (oldLessons.size() != newLessons.size()) {
            Log.d("ScheduleCheckWorker", "Days differ by lesson count: " + oldLessons.size() + " vs " + newLessons.size());
            return false;
        }

        for (int i = 0; i < oldLessons.size(); i++) {
            LessonIndex oldLesson = oldLessons.get(i);
            LessonIndex newLesson = newLessons.get(i);

            if (!areLessonsEqual(oldLesson, newLesson)) {
                Log.d("ScheduleCheckWorker", "Lessons differ for " + oldDay.getDayOfWeek() + " at index " + i);
                return false;
            }
        }

        return true;
    }

    private boolean areLessonsEqual(LessonIndex oldLesson, LessonIndex newLesson) {
        if (!Objects.equals(oldLesson.getLessonStartTime(), newLesson.getLessonStartTime()) ||
                !Objects.equals(oldLesson.getLessonEndTime(), newLesson.getLessonEndTime())) {
            Log.d("ScheduleCheckWorker", "Lessons differ by time: " + oldLesson.getLessonStartTime() + " vs " + newLesson.getLessonStartTime());
            return false;
        }

        List<LessonItem> oldItems = oldLesson.getItems();
        List<LessonItem> newItems = newLesson.getItems();

        if (oldItems.size() != newItems.size()) {
            Log.d("ScheduleCheckWorker", "Lessons differ by item count: " + oldItems.size() + " vs " + newItems.size());
            return false;
        }

        for (int i = 0; i < oldItems.size(); i++) {
            LessonItem oldItem = oldItems.get(i);
            LessonItem newItem = newItems.get(i);

            if (!Objects.equals(oldItem.getLessonName(), newItem.getLessonName()) ||
                    !Objects.equals(oldItem.getTeacherName(), newItem.getTeacherName()) ||
                    !Objects.equals(oldItem.getClassroom(), newItem.getClassroom()) ||
                    !Objects.equals(oldItem.getComment(), newItem.getComment()) ||
                    !Objects.equals(oldItem.getSubgroup(), newItem.getSubgroup()) ||
                    !Objects.equals(oldItem.getWeekType(), newItem.getWeekType())) {
                Log.d("ScheduleCheckWorker", "Lesson items differ: " + oldItem.getLessonName() + " vs " + newItem.getLessonName());
                return false;
            }
        }

        return true;
    }
}