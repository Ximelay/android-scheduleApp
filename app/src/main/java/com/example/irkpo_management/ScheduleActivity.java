package com.example.irkpo_management;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
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
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.irkpo_management.adapters.LessonAdapter;
import com.example.irkpo_management.models.DaySchedule;
import com.example.irkpo_management.models.DisplayLessonItem;
import com.example.irkpo_management.models.LessonIndex;
import com.example.irkpo_management.models.ScheduleResponse;
import com.example.irkpo_management.network.ApiClient;
import com.example.irkpo_management.network.ScheduleApi;
import com.example.irkpo_management.utils.DataProvider;
import com.example.irkpo_management.utils.GroupUtils;
import com.example.irkpo_management.utils.PreferenceManager;
import com.example.irkpo_management.utils.StickyHeaderDecoration;
import com.example.irkpo_management.utils.TeacherUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {

    private static final String TAG = "ScheduleActivity";
    private RecyclerView recyclerView;
    private TextView tvWeekType;
    private TextView tvWeekRange;
    private TextView tvCacheStatus;
    private AutoCompleteTextView etSearch;
    private Button btnSearch;
    private ProgressBar loadingProgressBar;
    private ArrayAdapter<String> adapterSearch;
    private List<String> searchList;
    private LessonAdapter lessonAdapter;
    private StickyHeaderDecoration stickyHeaderDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_schedule);

        GroupUtils.init(this, new DataProvider.LoadCallback<Integer>() {
            @Override
            public void onSuccess(Map<String, Integer> map) {
                Log.d("ScheduleActivity", "Groups loaded successfully");
                tryAutoLoadSchedule();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ScheduleActivity", "Failed to load groups", t);
                Toast.makeText(ScheduleActivity.this, "Ошибка загрузки групп", Toast.LENGTH_SHORT).show();
            }
        });

        TeacherUtils.init(this, new DataProvider.LoadCallback<String>() {
            @Override
            public void onSuccess(Map<String, String> map) {
                Log.d("ScheduleActivity", "Teachers loaded successfully");
                tryAutoLoadSchedule();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ScheduleActivity", "Failed to load teachers", t);
                Toast.makeText(ScheduleActivity.this, "Ошибка загрузки преподавателей", Toast.LENGTH_SHORT).show();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        PreferenceManager preferenceManager = new PreferenceManager(this);
        String lastSelectionType = preferenceManager.getLastSelectionType();

        recyclerView = findViewById(R.id.recyclerView);
        tvWeekType = findViewById(R.id.tvWeekType);
        tvWeekRange = findViewById(R.id.tvWeekRange);
        tvCacheStatus = findViewById(R.id.tvCacheStatus);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        searchList = new ArrayList<>();

        adapterSearch = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, searchList);
        etSearch.setAdapter(adapterSearch);
        etSearch.setThreshold(1);
        etSearch.setDropDownHeight((int) (getResources().getDisplayMetrics().density * 250));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true); // Устанавливаем фиксированный размер
        recyclerView.setItemViewCacheSize(20); // Увеличиваем кэш ViewHolder'ов

        lessonAdapter = new LessonAdapter(this, new ArrayList<>(), false);
        recyclerView.setAdapter(lessonAdapter);
        stickyHeaderDecoration = new StickyHeaderDecoration(lessonAdapter);
        recyclerView.addItemDecoration(stickyHeaderDecoration);

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();

            if (!query.isEmpty()) {
                if (GroupUtils.getGroupId(query) != null) {
                    Integer groupId = GroupUtils.getGroupId(query);
                    Log.d(TAG, "Search button clicked, processing for groupId: " + groupId);
                    if (isNetworkAvailable()) {
                        fetchSchedule(groupId, true);
                    } else {
                        loadCachedSchedule(groupId);
                    }
                    hideKeyboard();
                } else {
                    String teacherId = findTeacherIdByFormattedName(query);
                    if (teacherId != null) {
                        Log.d(TAG, "Search button clicked, fetching schedule for teacherId: " + teacherId);
                        if (isNetworkAvailable()) {
                            fetchScheduleByTeacher(teacherId, true);
                        } else {
                            loadCachedTeacherSchedule(teacherId);
                        }
                        hideKeyboard();
                    } else {
                        List<String> filteredTeachers = TeacherUtils.getFilteredTeachers(query);
                        if (!filteredTeachers.isEmpty()) {
                            String firstMatch = filteredTeachers.get(0);
                            teacherId = findTeacherIdByFormattedName(firstMatch);
                            if (teacherId != null) {
                                Log.d(TAG, "Search button clicked, fetching schedule for teacherId: " + teacherId + " (matched: " + firstMatch + ")");
                                if (isNetworkAvailable()) {
                                    fetchScheduleByTeacher(teacherId, true);
                                } else {
                                    loadCachedTeacherSchedule(teacherId);
                                }
                                etSearch.setText(firstMatch);
                                hideKeyboard();
                                return;
                            }
                        }
                        Toast.makeText(this, "Группа или преподаватель не найдены!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Пожалуйста, введите группу или преподавателя", Toast.LENGTH_SHORT).show();
            }
        });

        etSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            hideKeyboard();

            if (GroupUtils.getGroupId(selected) != null) {
                Integer groupId = GroupUtils.getGroupId(selected);
                Log.d(TAG, "Item selected, fetching schedule for groupId: " + groupId);
                if (isNetworkAvailable()) {
                    fetchSchedule(groupId, false);
                } else {
                    loadCachedSchedule(groupId);
                }
            } else {
                String teacherId = findTeacherIdByFormattedName(selected);
                if (teacherId != null) {
                    Log.d(TAG, "Item selected, fetching schedule for teacherId: " + teacherId);
                    if (isNetworkAvailable()) {
                        fetchScheduleByTeacher(teacherId, false);
                    } else {
                        loadCachedTeacherSchedule(teacherId);
                    }
                } else {
                    Toast.makeText(this, "Группа или преподаватель не найдены!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString().trim();
                if (!query.isEmpty()) {
                    List<String> filtered = new ArrayList<>();
                    List<String> filteredGroups = GroupUtils.getFilteredGroups(query);
                    filtered.addAll(filteredGroups);
                    List<String> filteredTeachers = TeacherUtils.getFilteredTeachers(query);
                    filtered.addAll(filteredTeachers);

                    adapterSearch.clear();
                    adapterSearch.addAll(filtered);
                    adapterSearch.notifyDataSetChanged();
                    Log.d(TAG, "Filtered items for query '" + query + "': " + filtered.size() +
                            " items (Groups: " + filteredGroups.size() + ", Teachers: " + filteredTeachers.size() + ")");
                    if (filteredTeachers.size() > 0) {
                        Log.d(TAG, "Teachers: " + filteredTeachers);
                    }
                    if (filtered.isEmpty()) {
                        Log.w(TAG, "No results found for query: '" + query + "'");
                    }
                } else {
                    adapterSearch.clear();
                    adapterSearch.addAll(searchList);
                    adapterSearch.notifyDataSetChanged();
                    Log.d(TAG, "Cleared search results");
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private String findTeacherIdByFormattedName(String formattedName) {
        for (Map.Entry<String, String> entry : TeacherUtils.getAllTeachers().entrySet()) {
            String formatted = formatTeacherName(entry.getKey());
            if (formatted.equals(formattedName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String formatTeacherName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length < 3) {
            return fullName;
        }
        String lastName = parts[0];
        String firstNameInitial = parts[1].substring(0, 1) + ".";
        String patronymicInitial = parts[2].substring(0, 1) + ".";
        return lastName + " " + firstNameInitial + patronymicInitial;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Notification permission granted");
            } else {
                Log.e(TAG, "Notification permission denied");
                Toast.makeText(this, "Разрешение на уведомления отклонено", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void fetchSchedule(int groupId, boolean addToFavorites) {
        showProgressBar();

        ScheduleApi api = ApiClient.getRetrofitInstance().create(ScheduleApi.class);
        api.getSchedule(groupId).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                hideProgressBar();
                if (response.isSuccessful() && response.body() != null) {
                    ScheduleResponse scheduleResponse = response.body();
                    displaySchedule(scheduleResponse, false);

                    PreferenceManager preferenceManager = new PreferenceManager(ScheduleActivity.this);

                    // Сохраняем данные в кэш в любом случае
                    Gson gson = new Gson();
                    String scheduleJson = gson.toJson(scheduleResponse);
                    preferenceManager.setScheduleCache(scheduleJson);
                    preferenceManager.setCachedGroupId(groupId);
                    Log.d(TAG, "Saved schedule to cache for groupId: " + groupId);

                    // Добавляем в избранное только если запрошено
                    if (addToFavorites) {
                        Log.d(TAG, "Saving groupId: " + groupId);
                        preferenceManager.setGroupId(groupId);
                        String groupName = GroupUtils.getGroupName(groupId);
                        preferenceManager.setDefaultGroup(groupName);
                        preferenceManager.setLastSelectionType(PreferenceManager.TYPE_GROUP);
                        Toast.makeText(ScheduleActivity.this, "Группа " + groupName + " добавлена в избранное", Toast.LENGTH_SHORT).show();
                    }

                    schedulePeriodWorker();
                } else {
                    Log.e(TAG, "Empty or failed response: " + response.code());
                    Toast.makeText(ScheduleActivity.this, "Ошибка загрузки расписания", Toast.LENGTH_SHORT).show();
                    loadCachedSchedule(groupId);
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                hideProgressBar();
                Log.e(TAG, "Failed to fetch schedule", t);
                Toast.makeText(ScheduleActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                loadCachedSchedule(groupId);
            }
        });
    }

    private void fetchScheduleByTeacher(String personId, boolean addToFavorites) {
        showProgressBar();

        ScheduleApi api = ApiClient.getRetrofitInstance().create(ScheduleApi.class);
        api.getScheduleByPersonId(personId).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                hideProgressBar();
                if (response.isSuccessful() && response.body() != null) {
                    ScheduleResponse scheduleResponse = response.body();
                    Log.d(TAG, "Teacher schedule response: " + new Gson().toJson(scheduleResponse));
                    displaySchedule(scheduleResponse, true);

                    // Кэшируем расписание в любом случае
                    PreferenceManager preferenceManager = new PreferenceManager(ScheduleActivity.this);
                    Gson gson = new Gson();
                    String scheduleJson = gson.toJson(scheduleResponse);
                    preferenceManager.setTeacherScheduleCache(scheduleJson);
                    preferenceManager.setCachedTeacherId(personId);
                    Log.d(TAG, "Saved teacher schedule to cache for ID: " + personId + ", JSON length: " + scheduleJson.length());

                    // Добавляем в избранное только если запрошено
                    if (addToFavorites) {
                        preferenceManager.setTeacherId(personId);
                        String teacherName = etSearch.getText().toString();
                        preferenceManager.setDefaultTeacher(teacherName);
                        preferenceManager.setLastSelectionType(PreferenceManager.TYPE_TEACHER);
                        Toast.makeText(ScheduleActivity.this, "Преподаватель " + teacherName + " добавлен в избранное", Toast.LENGTH_SHORT).show();
                    }

                    schedulePeriodWorker();
                } else {
                    Log.e(TAG, "Empty or failed response: " + response.code());
                    Toast.makeText(ScheduleActivity.this, "Ошибка загрузки расписания", Toast.LENGTH_SHORT).show();
                    loadCachedTeacherSchedule(personId);
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                hideProgressBar();
                Log.e(TAG, "Failed to fetch schedule", t);
                Toast.makeText(ScheduleActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                loadCachedTeacherSchedule(personId);
            }
        });
    }

    private void displaySchedule(ScheduleResponse scheduleResponse, boolean isTeacherSchedule) {
        tvCacheStatus.setVisibility(isNetworkAvailable() ? View.GONE : View.VISIBLE);

        List<DaySchedule> daySchedules = scheduleResponse.getItems();

        if (daySchedules.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            TextView noDataTextView = findViewById(R.id.tvNoData);
            noDataTextView.setVisibility(View.VISIBLE);
            noDataTextView.setText("Нет данных");
        } else {
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

            // Обновляем данные в существующем адаптере
            lessonAdapter.updateData(displayItems, isTeacherSchedule);
        }
    }

    private void loadCachedSchedule(int groupId) {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String cachedSchedule = preferenceManager.getScheduleCache();
        int cachedGroupId = preferenceManager.getCachedGroupId();

        if (cachedSchedule != null && !cachedSchedule.isEmpty() && cachedGroupId == groupId) {
            try {
                Gson gson = new Gson();
                ScheduleResponse scheduleResponse = gson.fromJson(cachedSchedule, ScheduleResponse.class);
                displaySchedule(scheduleResponse, false); // Передаем false для группы
                Toast.makeText(this, "Загружено кэшированное расписание (оффлайн)", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse cached schedule", e);
                Toast.makeText(this, "Ошибка загрузки кэшированного расписания", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Нет кэшированных данных для этой группы", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadCachedTeacherSchedule(String teacherId) {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String cachedSchedule = preferenceManager.getTeacherScheduleCache();
        String cachedTeacherId = preferenceManager.getCachedTeacherId();

        // Добавьте логи для отладки
        Log.d(TAG, "Loading cached teacher schedule. Requested ID: " + teacherId + ", Cached ID: " + cachedTeacherId + ", Schedule length: " + (cachedSchedule != null ? cachedSchedule.length() : "null"));

        if (cachedSchedule != null && !cachedSchedule.isEmpty() && cachedTeacherId != null && cachedTeacherId.equalsIgnoreCase(teacherId)) {
            try {
                Gson gson = new Gson();
                ScheduleResponse scheduleResponse = gson.fromJson(cachedSchedule, ScheduleResponse.class);
                displaySchedule(scheduleResponse, true);
                Toast.makeText(this, "Загружено кэшированное расписание (оффлайн)", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse cached teacher schedule", e);
                Toast.makeText(this, "Ошибка загрузки кэшированного расписания", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Нет кэшированных данных для этого преподавателя", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgressBar() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(200);
        loadingProgressBar.startAnimation(fadeIn);
    }

    private void hideProgressBar() {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(200);
        loadingProgressBar.startAnimation(fadeOut);
        loadingProgressBar.setVisibility(View.GONE);
    }

    private void schedulePeriodWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                com.example.irkpo_management.work.ScheduleCheckWorker.class,
                30, TimeUnit.MINUTES
        )
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "ScheduleCheck",
                ExistingPeriodicWorkPolicy.REPLACE,
                request
        );
        Log.d(TAG, "Scheduled periodic worker: " + request.getId());

        WorkManager.getInstance(this).getWorkInfosForUniqueWorkLiveData("ScheduleCheck")
                .observe(this, workInfos -> {
                    for (WorkInfo workInfo : workInfos) {
                        Log.d(TAG, "Work state: " + workInfo.getState());
                    }
                });
    }

    private void tryAutoLoadSchedule() {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String lastSelectionType = preferenceManager.getLastSelectionType();

        if (PreferenceManager.TYPE_GROUP.equals(lastSelectionType)) {
            String defaultGroup = preferenceManager.getDefaultGroup();
            if (defaultGroup != null) {
                etSearch.setText(defaultGroup);
                Integer groupId = GroupUtils.getGroupId(defaultGroup);
                if (groupId != null) {
                    Log.d(TAG, "Loading schedule for default group: " + defaultGroup + ", groupId: " + groupId);
                    if (isNetworkAvailable()) {
                        fetchSchedule(groupId, false);
                    } else {
                        loadCachedSchedule(groupId);
                    }
                }
            }
        } else if (PreferenceManager.TYPE_TEACHER.equals(lastSelectionType)) {
            String defaultTeacher = preferenceManager.getDefaultTeacher();
            String teacherId = preferenceManager.getTeacherId();
            if (defaultTeacher != null && teacherId != null) {
                etSearch.setText(defaultTeacher);
                Log.d(TAG, "Loading schedule for default teacher: " + defaultTeacher + ", teacherId: " + teacherId);
                if (isNetworkAvailable()) {
                    fetchScheduleByTeacher(teacherId, false);
                } else {
                    loadCachedTeacherSchedule(teacherId);
                }
            }
        }
    }
}