package com.example.sheduleapp_v5.work;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
public class ReminderScheduler {
    public static void scheduleReminder(Context context, String noteId, String noteText, long remindAtMillis) {
        long delay = remindAtMillis - System.currentTimeMillis();

        if(delay <= 0)
            return;

        Data inputData = new Data.Builder()
                .putString("note_text", noteText)
                .build();

        WorkRequest reminderRequest = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("reminder_" + noteId)
                .build();

        WorkManager.getInstance(context).enqueue(reminderRequest);
    }

    public static void scheduleWeeklyCleanup(Context context) {
        Calendar now = Calendar.getInstance();
        Calendar nextMonday = Calendar.getInstance();
        nextMonday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        nextMonday.set(Calendar.HOUR_OF_DAY, 0);
        nextMonday.set(Calendar.MINUTE, 0);
        nextMonday.set(Calendar.SECOND, 0);
        nextMonday.set(Calendar.MILLISECOND, 0);

        if(now.after(nextMonday)) {
            nextMonday.add(Calendar.WEEK_OF_YEAR, 1);
        }

        long initialDelay = nextMonday.getTimeInMillis() - System.currentTimeMillis();

        PeriodicWorkRequest cleanupRequest = new PeriodicWorkRequest.Builder(
                NoteCleanupWorker.class,
                7, TimeUnit.DAYS
        )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .addTag("weekly_cleanup")
                .build();

        WorkManager.getInstance(context).enqueue(cleanupRequest);
    }
}
