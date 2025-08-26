package com.example.irkpo_management.work;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.irkpo_management.R;
import com.example.irkpo_management.models.DaySchedule;
import com.example.irkpo_management.models.LessonIndex;
import com.example.irkpo_management.models.LessonItem;
import com.example.irkpo_management.models.ScheduleResponse;
import com.example.irkpo_management.network.ApiClient;
import com.example.irkpo_management.network.ScheduleApi;
import com.example.irkpo_management.utils.PreferenceManager;
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

            boolean groupResult = checkGroupSchedule(preferenceManager);
            boolean teacherResult = checkTeacherSchedule(preferenceManager);

            if (groupResult || teacherResult) {
                return Result.success();
            } else if (preferenceManager.getGroupId() == -1 && preferenceManager.getTeacherId() == null) {
                Log.d("ScheduleCheckWorker", "No group or teacher ID set, skipping checks");
                return Result.success(); // Или Result.failure(), в зависимости от логики
            }
            return Result.retry();
        } catch (Exception e) {
            Log.e("ScheduleCheckWorker", "Worker failed", e);
            return Result.retry();
        }
    }

    private boolean checkGroupSchedule(PreferenceManager preferenceManager) {
        int groupId = preferenceManager.getGroupId();

        if (groupId == -1) {
            Log.d("ScheduleCheckWorker", "GroupId not set, skipping group check");
            return false;
        }

        Log.d("ScheduleCheckWorker", "Checking schedule for GroupId: " + groupId);

        try {
            ScheduleApi api = ApiClient.getRetrofitInstance().create(ScheduleApi.class);
            Call<ScheduleResponse> call = api.getSchedule(groupId);
            Response<ScheduleResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                Gson gson = new Gson();
                ScheduleResponse newSchedule = response.body();
                String newScheduleJson = gson.toJson(newSchedule);

                int cachedGroupId = preferenceManager.getCachedGroupId();
                String oldScheduleJson = preferenceManager.getScheduleCache();

                if (newScheduleJson != null && !newScheduleJson.equals("{}")) {
                    preferenceManager.setScheduleCache(newScheduleJson);
                    preferenceManager.setCachedGroupId(groupId);
                } else {
                    Log.e("ScheduleCheckWorker", "Invalid new schedule JSON for groupId: " + groupId);
                    return false;
                }

                if (oldScheduleJson.isEmpty() || cachedGroupId != groupId) {
                    return true;
                }

                ScheduleResponse oldSchedule = gson.fromJson(oldScheduleJson, ScheduleResponse.class);
                if (oldSchedule != null && oldSchedule.getItems() != null) {
                    List<String> changedDays = getChangedDays(oldSchedule, newSchedule);

                    if (!changedDays.isEmpty()) {
                        String groupName = preferenceManager.getDefaultGroup();
                        String days = String.join(", ", changedDays);
                        String message = "У группы " + (groupName != null ? groupName : "неизвестно") +
                                " изменилось расписание на " + days;
                        sendNotification(message);
                    }
                }
                return true;
            } else {
                Log.e("ScheduleCheckWorker", "API response failed for groupId " + groupId + ": " + response.code());
            }
        } catch (Exception e) {
            Log.e("ScheduleCheckWorker", "Error checking group schedule for groupId " + groupId, e);
        }
        return false;
    }

    private boolean checkTeacherSchedule(PreferenceManager preferenceManager) {
        String teacherId = preferenceManager.getTeacherId();

        if (teacherId == null) {
            Log.d("ScheduleCheckWorker", "TeacherId not set, skipping teacher check");
            return false;
        }

        Log.d("ScheduleCheckWorker", "Checking schedule for TeacherId: " + teacherId);

        try {
            ScheduleApi api = ApiClient.getRetrofitInstance().create(ScheduleApi.class);
            Call<ScheduleResponse> call = api.getScheduleByPersonId(teacherId);
            Response<ScheduleResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                Gson gson = new Gson();
                ScheduleResponse newSchedule = response.body();
                String newScheduleJson = gson.toJson(newSchedule);

                String cachedTeacherId = preferenceManager.getCachedTeacherId();
                String oldScheduleJson = preferenceManager.getTeacherScheduleCache();

                // Сохраняем новое расписание в кэш
                if (newScheduleJson != null && !newScheduleJson.equals("{}")) {
                    preferenceManager.setTeacherScheduleCache(newScheduleJson);
                    preferenceManager.setCachedTeacherId(teacherId);
                } else {
                    return false;
                }

                // Пропускаем уведомление при первой загрузке или смене преподавателя
                if (oldScheduleJson.isEmpty() || !teacherId.equals(cachedTeacherId)) {
                    return true;
                }

                // Проверяем изменения
                ScheduleResponse oldSchedule = gson.fromJson(oldScheduleJson, ScheduleResponse.class);
                if (oldSchedule != null && oldSchedule.getItems() != null) {
                    List<String> changedDays = getChangedDays(oldSchedule, newSchedule);

                    if (!changedDays.isEmpty()) {
                        String teacherName = preferenceManager.getDefaultTeacher();
                        String days = String.join(", ", changedDays);
                        String message = "У преподавателя " + (teacherName != null ? teacherName : "неизвестно") +
                                " изменилось расписание на " + days;

                        sendNotification(message);
                    }
                }
                return true;
            }
        } catch (Exception e) {
            Log.e("ScheduleCheckWorker", "Error checking teacher schedule", e);
        }
        return false;
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