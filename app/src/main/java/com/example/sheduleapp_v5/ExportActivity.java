package com.example.sheduleapp_v5;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.sheduleapp_v5.export.SendToTelegramTask;
import com.example.sheduleapp_v5.models.PerformanceResponse;
import com.example.sheduleapp_v5.network.ApiClient;
import com.example.sheduleapp_v5.network.PerformanceApi;
import com.example.sheduleapp_v5.utils.ExportUtils;
import com.example.sheduleapp_v5.utils.PreferenceManager;
import com.example.sheduleapp_v5.utils.TeacherUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExportActivity extends AppCompatActivity {

    public AutoCompleteTextView semesterSpinner;
    private TextInputLayout semesterInputLayout;
    private Button btnSelectOptions, btnExportPdf, btnExportExcel, btnSendTelegram, btnRetry;
    private ProgressBar loadingProgressBar;
    private List<PerformanceResponse.Plan> allPlans;
    private List<String> semesterNames;
    private PreferenceManager preferenceManager;

    // Переменные для хранения выбранных опций
    private boolean exportSubjects = true;
    private boolean exportLessons = true;
    private boolean exportTeachers = false;
    private boolean exportAttestation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_export);

        semesterInputLayout = findViewById(R.id.semester_input_layout);
        semesterSpinner = findViewById(R.id.semesterSpinner);
        btnSelectOptions = findViewById(R.id.btn_select_options);
        btnExportPdf = findViewById(R.id.btn_export_pdf);
        btnExportExcel = findViewById(R.id.btn_export_excel);
        btnSendTelegram = findViewById(R.id.btn_send_telegram);
        btnRetry = findViewById(R.id.btn_retry);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        preferenceManager = new PreferenceManager(this);
        TeacherUtils.init(this);

        // Очистка кэша, если прошло больше часа
        clearCacheIfExpired();

        String phoneNumber = preferenceManager.getPhoneNumber();
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String rawPhone = phoneNumber.replaceAll("[^\\d]", "");
            if (rawPhone.startsWith("7") && rawPhone.length() == 11) {
                rawPhone = rawPhone.substring(1);
            }
            Log.d("ExportActivity", "Phone number: " + rawPhone);
            fetchPerformanceData(rawPhone);
        } else {
            Toast.makeText(this, "Введите номер телефона в разделе Успеваемости", Toast.LENGTH_LONG).show();
            finish();
        }

        btnSelectOptions.setOnClickListener(v -> showExportOptionsDialog());
        btnExportPdf.setOnClickListener(v -> ExportUtils.exportToPdf(this, allPlans, exportSubjects, exportLessons, exportTeachers, exportAttestation));
        btnExportExcel.setOnClickListener(v -> {
            if (allPlans != null) {
                ExportUtils.exportToExcel(this, allPlans, semesterSpinner.getText().toString(), exportSubjects, exportLessons, exportTeachers, exportAttestation, null);
            } else {
                Toast.makeText(this, "Нет данных для экспорта", Toast.LENGTH_SHORT).show();
            }
        });
        btnSendTelegram.setOnClickListener(v -> new SendToTelegramTask(this, allPlans, semesterSpinner.getText().toString(), exportSubjects, exportLessons, exportTeachers, exportAttestation).execute());
        btnRetry.setOnClickListener(v -> {
            btnRetry.setVisibility(View.GONE);
            String phoneNumberRetry = preferenceManager.getPhoneNumber();
            if (phoneNumberRetry != null && !phoneNumberRetry.isEmpty()) {
                String rawPhone = phoneNumberRetry.replaceAll("[^\\d]", "");
                if (rawPhone.startsWith("7") && rawPhone.length() == 11) {
                    rawPhone = rawPhone.substring(1);
                }
                Log.d("ExportActivity", "Retry phone number: " + rawPhone);
                fetchPerformanceData(rawPhone);
            }
        });

        semesterSpinner.setOnItemClickListener((parent, view, position, id) -> {
            if (allPlans != null) {
                // Обновление данных при выборе семестра
            }
        });
    }

    private void clearCacheIfExpired() {
        long lastCacheTime = preferenceManager.getLastCacheTime();
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCacheTime > 60 * 60 * 1000) { // 1 час = 3600000 мс
            preferenceManager.setPerformanceCache("");
            preferenceManager.setLastCacheTime(currentTime);
            Log.d("ExportActivity", "Cache cleared due to expiration");
        }
    }

    private void showExportOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_export_options, null);

        CheckBox cbSubjects = dialogView.findViewById(R.id.cb_subjects);
        CheckBox cbLessons = dialogView.findViewById(R.id.cb_lessons);
        CheckBox cbTeachers = dialogView.findViewById(R.id.cb_teachers);
        CheckBox cbAttestation = dialogView.findViewById(R.id.cb_attestation);

        cbSubjects.setChecked(exportSubjects);
        cbLessons.setChecked(exportLessons);
        cbTeachers.setChecked(exportTeachers);
        cbAttestation.setChecked(exportAttestation);

        builder.setView(dialogView)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    exportSubjects = cbSubjects.isChecked();
                    exportLessons = cbLessons.isChecked();
                    exportTeachers = cbTeachers.isChecked();
                    exportAttestation = cbAttestation.isChecked();
                    Toast.makeText(this, "Опции сохранены", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void fetchPerformanceData(String rawPhone) {
        Log.d("ExportActivity", "Fetching performance for phone: " + rawPhone);

        String cachedData = preferenceManager.getPerformanceCache();
        if (cachedData != null && !cachedData.isEmpty()) {
            try {
                PerformanceResponse cachedResponse = new Gson().fromJson(cachedData, PerformanceResponse.class);
                if (cachedResponse != null && cachedResponse.getPlans() != null) {
                    allPlans = cachedResponse.getPlans();
                    setupSemesterSpinner(allPlans);
                    btnRetry.setVisibility(View.GONE);
                    Toast.makeText(this, "Загружены кэшированные данные", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                    semesterInputLayout.setVisibility(View.VISIBLE);
                    return;
                }
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка чтения кэша: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ExportActivity", "Cache error", e);
            }
        }

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();
            btnRetry.setVisibility(View.VISIBLE);
            hideProgressBar();
            return;
        }

        showProgressBar();
        PerformanceApi apiService = ApiClient.getRetrofitInstance().create(PerformanceApi.class);
        Call<PerformanceResponse> call = apiService.getPerformance(rawPhone);
        call.enqueue(new Callback<PerformanceResponse>() {
            @Override
            public void onResponse(Call<PerformanceResponse> call, Response<PerformanceResponse> response) {
                hideProgressBar();
                Log.d("ExportActivity", "HTTP Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null && response.body().getPlans() != null) {
                    allPlans = response.body().getPlans();
                    preferenceManager.setPerformanceCache(new Gson().toJson(response.body()));
                    preferenceManager.setLastCacheTime(System.currentTimeMillis());
                    setupSemesterSpinner(allPlans);
                    btnRetry.setVisibility(View.GONE);
                    semesterInputLayout.setVisibility(View.VISIBLE);
                } else {
                    String errorMsg = "Ошибка загрузки данных";
                    if (!response.isSuccessful()) {
                        errorMsg += ": код " + response.code();
                        try {
                            errorMsg += ", ошибка: " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e("ExportActivity", "Error reading error body", e);
                        }
                    } else if (response.body() == null) {
                        errorMsg += ": пустой ответ";
                    } else if (response.body().getPlans() == null) {
                        errorMsg += ": нет данных об успеваемости";
                    }
                    Toast.makeText(ExportActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    btnRetry.setVisibility(View.VISIBLE);
                    Log.e("ExportActivity", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<PerformanceResponse> call, Throwable t) {
                hideProgressBar();
                String errorMsg = "Ошибка сети: " + t.getMessage();
                Toast.makeText(ExportActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                btnRetry.setVisibility(View.VISIBLE);
                Log.e("ExportActivity", errorMsg, t);
            }
        });
    }

    private void setupSemesterSpinner(List<PerformanceResponse.Plan> plans) {
        semesterNames = getSemesterNames(plans);
        if (!semesterNames.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, semesterNames);
            semesterSpinner.setAdapter(adapter);
            semesterSpinner.setText(semesterNames.get(semesterNames.size() - 1), false);
            Log.d("ExportActivity", "Semester spinner set with " + semesterNames.size() + " items");
        } else {
            Toast.makeText(this, "Семестры не найдены", Toast.LENGTH_SHORT).show();
            semesterInputLayout.setVisibility(View.GONE);
        }
    }

    private List<String> getSemesterNames(List<PerformanceResponse.Plan> plans) {
        Set<String> names = new HashSet<>();
        if (plans != null) {
            for (PerformanceResponse.Plan plan : plans) {
                for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
                    names.add(period.getName());
                }
            }
        }
        List<String> sortedNames = new ArrayList<>(names);
        sortedNames.sort(String::compareTo);
        Log.d("ExportActivity", "Found semesters: " + sortedNames);
        return sortedNames;
    }

    public void showProgressBar() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(200);
        loadingProgressBar.startAnimation(fadeIn);
    }

    public void hideProgressBar() {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(200);
        loadingProgressBar.startAnimation(fadeOut);
        loadingProgressBar.setVisibility(View.GONE);
    }
}