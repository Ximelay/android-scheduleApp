package com.example.sheduleapp_v5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sheduleapp_v5.adapters.ScheduleAdapter;
import com.example.sheduleapp_v5.models.DaySchedule;
import com.example.sheduleapp_v5.models.LessonIndex;
import com.example.sheduleapp_v5.models.LessonItem;
import com.example.sheduleapp_v5.models.ScheduleResponse;
import com.example.sheduleapp_v5.network.ApiClient;
import com.example.sheduleapp_v5.network.ScheduleApi;
import com.example.sheduleapp_v5.work.ReminderScheduler;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button buttonSchedule;
    private Button buttonPerformance;
    private Button buttonMoodle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReminderScheduler.scheduleWeeklyCleanup(this);


        buttonSchedule = findViewById(R.id.button_schedule);
        buttonPerformance = findViewById(R.id.button_performance);
        buttonMoodle = findViewById(R.id.button_moodle);

        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

        buttonPerformance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PerformanceActivity.class);
                startActivity(intent);
            }
        });

        buttonMoodle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MoodleActivity.class);
                startActivity(intent);
            }
        });
    }
}
