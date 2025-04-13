package com.example.sheduleapp_v5.work;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sheduleapp_v5.R;
import com.example.sheduleapp_v5.db.NoteRepository;

public class ReminderWorker extends Worker {
    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String noteId = getInputData().getString("note_id");
        String noteText = getInputData().getString("note_text");
        String lessonName = getInputData().getString("lesson_name");
        String lessonTime = getInputData().getString("lesson_time");

        String notificationText = String.format("Заметка: %s\nПара: %s\nВремя: %s",
                noteText, lessonName, lessonTime);

        // Если NotificationHelper не реализован, используем этот код:
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "note_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Напоминание")
                .setContentText(notificationText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }

        if(noteId != null && !noteId.isEmpty()) {
            NoteRepository repository = new NoteRepository(getApplicationContext());
            repository.deleteNote(noteId);
        }

        return Result.success();
    }
}
