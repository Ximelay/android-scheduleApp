package com.example.sheduleapp_v5;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.sheduleapp_v5.utils.GroupUtils;
import com.example.sheduleapp_v5.utils.StickyHeaderDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private TextView tvWeekType;
    private TextView tvWeekRange;
    private AutoCompleteTextView etGroupId;
    private Button btnSearchGroup;
    private ArrayAdapter<String> adapterGroup;
    private List<String> groupNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        recyclerView = findViewById(R.id.recyclerView);
        tvWeekType = findViewById(R.id.tvWeekType);
        tvWeekRange = findViewById(R.id.tvWeekRange);
        etGroupId = findViewById(R.id.etGroupId);
        btnSearchGroup = findViewById(R.id.btnSearchGroup);

        groupNames = new ArrayList<>(GroupUtils.getAllGroups().keySet());

        // Инициализация адаптера для AutoCompleteTextView
        adapterGroup = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, groupNames);
        etGroupId.setAdapter(adapterGroup);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Поиск группы по кнопке
        btnSearchGroup.setOnClickListener(v -> {
            String groupIdStr = etGroupId.getText().toString().trim();

            if (!groupIdStr.isEmpty()) {
                Integer groupId = GroupUtils.getGroupId(groupIdStr);
                if (groupId != null) {
                    fetchSchedule(groupId);
                } else {
                    Toast.makeText(this, "Группа не найдена!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Пожалуйста, введите номер группы", Toast.LENGTH_SHORT).show();
            }
        });

        // Настройка слушателя для выбора группы
        etGroupId.setThreshold(1);
        etGroupId.setOnItemClickListener((parent, view, position, id) -> {
            String selectedGroup = (String) parent.getItemAtPosition(position);
            Integer groupId = GroupUtils.getGroupId(selectedGroup);
            fetchSchedule(groupId);
        });

        // Добавление обработчика текстового изменения в поле ввода
        etGroupId.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString().trim();
                if (!query.isEmpty()) {
                    List<String> filteredGroups = GroupUtils.getFilteredGroups(query);
                    // Обновляем данные адаптера без пересоздания его
                    adapterGroup.clear();
                    adapterGroup.addAll(filteredGroups);
                    adapterGroup.notifyDataSetChanged(); // Уведомляем об изменении данных
                } else {
                    adapterGroup.clear();
                    adapterGroup.addAll(groupNames);
                    adapterGroup.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });
    }

    // Получения расписания группы
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