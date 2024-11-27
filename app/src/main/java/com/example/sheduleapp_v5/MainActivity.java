package com.example.sheduleapp_v5;

import android.os.Bundle;
import android.util.Log;
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
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ScheduleApi api = ApiClient.getRetrofitInstance().create(ScheduleApi.class);
        api.getSchedule(732).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Логируем полученный JSON
                    Log.d("API Response", "Response Body: " + response.body().toString());

                    // Получаем список всех дней с расписанием
                    List<DaySchedule> daySchedules = response.body().getItems();

                    // Логируем каждый день и уроки
                    for (DaySchedule day : daySchedules) {
                        Log.d("DaySchedule", "Day: " + day.getDayOfWeek() + ", WeekType: " + day.getWeekType());
                        if (day.getLessonIndexes() != null) {
                            for (LessonIndex lessonIndex : day.getLessonIndexes()) {
                                for (LessonItem lessonItem : lessonIndex.getItems()) {
                                    Log.d("LessonItem", "Lesson: " + lessonItem.getLessonName());
                                }
                            }
                        }
                    }

                    // Создаем адаптер с полученными данными
                    adapter = new ScheduleAdapter(daySchedules);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("API Error", "Response is empty or unsuccessful.");
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                Log.e("Api error", "Failed to load schedule", t);
            }
        });
    }
}
