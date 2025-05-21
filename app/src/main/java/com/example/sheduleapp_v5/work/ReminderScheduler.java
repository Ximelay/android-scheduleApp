package com.example.sheduleapp_v5.work;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

public class ReminderScheduler {
    public static void scheduleReminder(Context context, String noteId, String noteText,
                                        String lessonName, String lessonTime, long remindAtMillis) {
        long delay = remindAtMillis - System.currentTimeMillis();

        if (delay <= 0)
            return;

        Data inputData = new Data.Builder()
                .putString("note_id", noteId)
                .putString("note_text", noteText)
                .putString("lesson_name", lessonName)
                .putString("lesson_time", lessonTime)
                .build();

        WorkRequest reminderRequest = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("reminder_" + noteId)
                .build();

        WorkManager.getInstance(context).enqueue(reminderRequest);
    }

    public static void cancelReminder(Context context, String noteId) {
        WorkManager.getInstance(context).cancelAllWorkByTag("reminder_" + noteId);
    }
}