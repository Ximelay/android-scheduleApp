package com.example.stud_informer_irkpo.db;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@RunWith(RobolectricTestRunner.class)
public class NoteDatabaseTest {

    @Test
    public void testGetInstance() {
        Context context = ApplicationProvider.getApplicationContext();
        NoteDatabase db = NoteDatabase.getInstance(context);

        assertNotNull(db);
        assertNotNull(db.noteDao());
    }

    @Test
    public void testSingletonInstance() {
        Context context = ApplicationProvider.getApplicationContext();
        NoteDatabase db1 = NoteDatabase.getInstance(context);
        NoteDatabase db2 = NoteDatabase.getInstance(context);

        assertSame(db1, db2);
    }
}
