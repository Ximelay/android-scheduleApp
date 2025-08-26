package com.example.irkpo_management.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.irkpo_management.db.NoteDatabase;

public class NoteCleanupWorker extends Worker {
    public NoteCleanupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        NoteDatabase.getInstance(getApplicationContext()).noteDao().deleteAllNotes();
        return Result.success();
    }
}
