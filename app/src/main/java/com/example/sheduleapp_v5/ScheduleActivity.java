package com.example.sheduleapp_v5;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvWeekType;
    private TextView tvWeekRange;
    private AutoCompleteTextView etSearch;
    private Button btnSearch;
    private ProgressBar loadingProgressBar;
    private ArrayAdapter<String> adapterSearch;
    private List<String> searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        GroupUtils.init(this);
        TeacherUtils.init(this);

        // Проверка и запрос разрешения на уведомления (Android 13+)
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
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        searchList = new ArrayList<>();

        // Инициализация адаптера для AutoCompleteTextView
        adapterSearch = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, searchList);
        etSearch.setAdapter(adapterSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String defaultGroup = preferenceManager.getDefaultGroup();
        if (defaultGroup != null) {
            etSearch.setText(defaultGroup);
            Integer groupId = GroupUtils.getGroupId(defaultGroup);
            if (groupId != null) {
                Log.d("ScheduleActivity", "Loading schedule for default group: " + defaultGroup + ", groupId: " + groupId);
                fetchSchedule(groupId);
            }
        }

        // Поиск группы по кнопке
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();

            if (!query.isEmpty()) {
                if (GroupUtils.getGroupId(query) != null) {
                    Integer groupId = GroupUtils.getGroupId(query);
                    Log.d("ScheduleActivity", "Search button clicked, fetching schedule for groupId: " + groupId);
                    fetchSchedule(groupId);
                } else if (TeacherUtils.getTeacherId(query) != null) {
                    String teacherId = TeacherUtils.getTeacherId(query);
                    Log.d("ScheduleActivity", "Search button clicked, fetching schedule for teacherId: " + teacherId);
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
                Log.d("ScheduleActivity", "Item selected, fetching schedule for groupId: " + groupId);
                fetchSchedule(groupId);
            } else if (TeacherUtils.getTeacherId(selected) != null) {
                String teacherId = TeacherUtils.getTeacherId(selected);
                Log.d("ScheduleActivity", "Item selected, fetching schedule for teacherId: " + teacherId);
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

    // Обработка результата запроса разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("ScheduleActivity", "Notification permission granted");
            } else {
                Log.e("ScheduleActivity", "Notification permission denied");
                Toast.makeText(this, "Разрешение на уведомления отклонено", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Получения расписания группы
    private void fetchSchedule(int groupId) {
        showProgressBar();

        ScheduleApi api = ApiClient.getRetrofitInstance().create(ScheduleApi.class);
        api.getSchedule(groupId).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                hideProgressBar();
                if (response.isSuccessful() && response.body() != null) {
                    ScheduleResponse scheduleResponse = response.body();
                    List<DaySchedule> daySchedules = scheduleResponse.getItems();

                    if (daySchedules.isEmpty()) {
                        // If no items are found, display the "нет данных" message and hide the RecyclerView
                        recyclerView.setVisibility(View.GONE);
                        TextView noDataTextView = findViewById(R.id.tvNoData);
                        noDataTextView.setVisibility(View.VISIBLE);
                        noDataTextView.setText("Нет данных");
                    } else {
                        // Otherwise, hide the "нет данных" message and show the RecyclerView
                        recyclerView.setVisibility(View.VISIBLE);
                        TextView noDataTextView = findViewById(R.id.tvNoData);
                        noDataTextView.setVisibility(View.GONE);

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

                        PreferenceManager preferenceManager = new PreferenceManager(ScheduleActivity.this);
                        Log.d("ScheduleActivity", "Saving groupId: " + groupId);
                        preferenceManager.setGroupId(groupId);
                        preferenceManager.setDefaultGroup(GroupUtils.getGroupName(groupId));

                        // Сохраняем расписание и cachedGroupId в кэш
                        Gson gson = new Gson();
                        String scheduleJson = gson.toJson(scheduleResponse);
                        preferenceManager.setScheduleCache(scheduleJson);
                        preferenceManager.setCachedGroupId(groupId);
                        Log.d("ScheduleActivity", "Saved schedule to cache for groupId: " + groupId);

                        schedulePeriodWorker();
                    }
                } else {
                    Log.e("ScheduleActivity", "Empty or failed response: " + response.code());
                    Toast.makeText(ScheduleActivity.this, "Ошибка загрузки расписания", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                hideProgressBar();
                Log.e("ScheduleActivity", "Failed to fetch schedule", t);
                Toast.makeText(ScheduleActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchScheduleByTeacher(String personId) {
        showProgressBar();

        ScheduleApi api = ApiClient.getRetrofitInstance().create(ScheduleApi.class);
        api.getScheduleByPersonId(personId).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                hideProgressBar();
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
                    Log.e("ScheduleActivity", "Empty or failed response: " + response.code());
                    Toast.makeText(ScheduleActivity.this, "Ошибка загрузки расписания", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                hideProgressBar();
                Log.e("ScheduleActivity", "Failed to fetch schedule", t);
                Toast.makeText(ScheduleActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressBar() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(200);  // Длительность анимации
        loadingProgressBar.startAnimation(fadeIn);
    }

    private void hideProgressBar() {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(200);  // Длительность анимации
        loadingProgressBar.startAnimation(fadeOut);
        loadingProgressBar.setVisibility(View.GONE);
    }

    private void schedulePeriodWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                com.example.sheduleapp_v5.work.ScheduleCheckWorker.class,
                30, TimeUnit.MINUTES
        )
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "ScheduleCheck",
                ExistingPeriodicWorkPolicy.REPLACE,
                request
        );
        Log.d("ScheduleActivity", "Scheduled periodic worker: ScheduleCheck");

        // Отладка состояния воркера
        WorkManager.getInstance(this).getWorkInfosForUniqueWorkLiveData("ScheduleCheck")
                .observe(this, workInfos -> {
                    for (WorkInfo workInfo : workInfos) {
                        Log.d("ScheduleActivity", "Work state: " + workInfo.getState());
                    }
                });
    }
}