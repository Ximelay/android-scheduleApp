package com.example.sheduleapp_v5;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sheduleapp_v5.adapters.ScheduleAdapter;
import com.example.sheduleapp_v5.models.DaySchedule;
import com.example.sheduleapp_v5.models.Lesson;
import com.example.sheduleapp_v5.models.LessonItem;
import com.example.sheduleapp_v5.models.ScheduleResponse;
import com.example.sheduleapp_v5.network.ApiClient;
import com.example.sheduleapp_v5.network.ScheduleApi;
import java.util.ArrayList;
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
                    // Получаем список всех LessonItem из всех дней
                    List<LessonItem> allLessons = new ArrayList<>();
                    for (DaySchedule daySchedule : response.body().getItems()) {
                        for (Lesson lesson : daySchedule.getLessonIndexes()) {
                            allLessons.addAll(lesson.getItems());
                        }
                    }

                    // Создаем адаптер с полученными данными
                    adapter = new ScheduleAdapter(allLessons);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                Log.e("Api error", "Failed to load schedule");
            }
        });
    }
}
