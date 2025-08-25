package com.example.stud_informer_irkpo.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.stud_informer_irkpo.db.NoteDatabase;

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
