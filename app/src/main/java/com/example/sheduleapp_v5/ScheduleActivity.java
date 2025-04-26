package com.example.sheduleapp_v5;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.example.sheduleapp_v5.utils.PreferenceManager;
import com.example.sheduleapp_v5.utils.StickyHeaderDecoration;
import com.example.sheduleapp_v5.utils.TeacherUtils;

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
    private AutoCompleteTextView etSearch;
    private Button btnSearch;
    private ArrayAdapter<String> adapterSearch;
    private List<String> searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        GroupUtils.init(this);
        TeacherUtils.init(this);

        // ✅ Проверка и запрос разрешения на уведомления (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        PreferenceManager preferenceManager = new PreferenceManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        tvWeekType = findViewById(R.id.tvWeekType);
        tvWeekRange = findViewById(R.id.tvWeekRange);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);

        searchList = new ArrayList<>();

        // Инициализация адаптера для AutoCompleteTextView
        adapterSearch = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, searchList);
        etSearch.setAdapter(adapterSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String defaultGroup = preferenceManager.getDefaultGroup();
        if(defaultGroup != null) {
            etSearch.setText(defaultGroup);
            Integer groupId = GroupUtils.getGroupId(defaultGroup);
            if(groupId != null) {
                fetchSchedule(groupId);
            }
        }

        // Поиск группы по кнопке
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();

            if(!query.isEmpty()) {
                if(GroupUtils.getGroupId(query) != null) {
                    Integer groupId = GroupUtils.getGroupId(query);
                    fetchSchedule(groupId);
                } else if(TeacherUtils.getTeacherId(query) != null) {
                    String teacherId = TeacherUtils.getTeacherId(query);
                    fetchScheduleByTeacher(teacherId);
                } else {
                    Toast.makeText(this, "Группа или преподаватель не найдены!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Пожалуйста, введите группу или фамилию преподавателя", Toast.LENGTH_SHORT).show();
            }
        });

        // Настройка слушателя для выбора группы
        etSearch.setThreshold(1);
        etSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);

            if (GroupUtils.getGroupId(selected) != null) {
                Integer groupId = GroupUtils.getGroupId(selected);
                fetchSchedule(groupId);
            } else if (TeacherUtils.getTeacherId(selected) != null) {
                String teacherId = TeacherUtils.getTeacherId(selected);
                fetchScheduleByTeacher(teacherId);
            } else {
                Toast.makeText(this, "Группа или преподаватель не найдены!", Toast.LENGTH_SHORT).show();
            }
        });

        // Добавление обработчика текстового изменения в поле ввода
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString().trim();
                if (!query.isEmpty()) {
                    List<String> filtered = new ArrayList<>();
                    filtered.addAll(GroupUtils.getFilteredGroups(query)); // сначала группы
                    filtered.addAll(TeacherUtils.getFilteredTeachers(query)); // потом преподаватели

                    adapterSearch.clear();
                    adapterSearch.addAll(filtered);
                    adapterSearch.notifyDataSetChanged();
                } else {
                    adapterSearch.clear();
                    adapterSearch.addAll(searchList);
                    adapterSearch.notifyDataSetChanged();
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

                    LessonAdapter lessonAdapter = new LessonAdapter(ScheduleActivity.this, displayItems);
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

    private void fetchScheduleByTeacher(String personId) {
        ScheduleApi api = ApiClient.getRetrofitInstance().create(ScheduleApi.class);
        api.getScheduleByPersonId(personId).enqueue(new Callback<ScheduleResponse>() {
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
                        displayItems.add(new DisplayLessonItem(
                                DisplayLessonItem.TYPE_HEADER,
                                day.getDayOfWeek(),
                                null,
                                null,
                                null,
                                false,
                                currentWeekType
                        ));

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

                    LessonAdapter lessonAdapter = new LessonAdapter(ScheduleActivity.this, displayItems);
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