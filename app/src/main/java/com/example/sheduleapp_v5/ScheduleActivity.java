package com.example.sheduleapp_v5;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sheduleapp_v5.adapters.LessonAdapter;
import com.example.sheduleapp_v5.adapters.ScheduleAdapter;
import com.example.sheduleapp_v5.models.DaySchedule;
import com.example.sheduleapp_v5.models.DisplayLessonItem;
import com.example.sheduleapp_v5.models.LessonIndex;
import com.example.sheduleapp_v5.models.ScheduleResponse;
import com.example.sheduleapp_v5.network.ApiClient;
import com.example.sheduleapp_v5.network.ScheduleApi;
import com.example.sheduleapp_v5.utils.StickyHeaderDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private TextView tvWeekType; // Для отображения типа недели
    private TextView tvWeekRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        recyclerView = findViewById(R.id.recyclerView);
        tvWeekType = findViewById(R.id.tvWeekType); // Для отображения типа недели
        tvWeekRange = findViewById(R.id.tvWeekRange);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Загружаем расписание для группы 732
        fetchSchedule(732); // Пример: группа 732
    }

    private void fetchSchedule(int groupId) {
        ScheduleApi api = ApiClient.getRetrofitInstance().create(ScheduleApi.class);
        api.getSchedule(groupId).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ScheduleResponse scheduleResponse = response.body();
                    List<DaySchedule> daySchedules = scheduleResponse.getItems();

                    int currentWeekType = scheduleResponse.getCurrentWeekType();

                    String weekLabel = currentWeekType == 1 ? "Круглая" : "Треугольная";
                    tvWeekType.setText("Тип недели: " + weekLabel);
                    tvWeekRange.setText("[" + scheduleResponse.getCurrentWeekName() + "]");

                    List<DisplayLessonItem> displayItems = new ArrayList<>();
                    for (DaySchedule day : daySchedules) {
                        // Добавляем заголовок дня
                        displayItems.add(new DisplayLessonItem(
                                DisplayLessonItem.TYPE_HEADER,
                                day.getDayOfWeek(),
                                null,
                                null,
                                null,
                                false,
                                currentWeekType
                        ));

                        // Добавляем пары
                        for (LessonIndex index : day.getLessonIndexes()) {
                            displayItems.add(new DisplayLessonItem(
                                    DisplayLessonItem.TYPE_LESSON,
                                    day.getDayOfWeek(),
                                    index.getLessonStartTime(),
                                    index.getLessonEndTime(),
                                    index.getItems(),
                                    false,
                                    currentWeekType
                            ));
                        }
                    }

                    LessonAdapter lessonAdapter = new LessonAdapter(displayItems);
                    recyclerView.setAdapter(lessonAdapter);
                    recyclerView.addItemDecoration(new StickyHeaderDecoration(lessonAdapter));

                } else {
                    Log.e("API", "Empty or failed response");
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                Log.e("API", "Failed to fetch schedule", t);
            }
        });
    }
}