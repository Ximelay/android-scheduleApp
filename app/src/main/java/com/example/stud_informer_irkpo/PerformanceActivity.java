package com.example.stud_informer_irkpo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stud_informer_irkpo.adapters.PerformanceAdapter;
import com.example.stud_informer_irkpo.models.PerformanceResponse;
import com.example.stud_informer_irkpo.network.ApiClient;
import com.example.stud_informer_irkpo.network.PerformanceApi;
import com.example.stud_informer_irkpo.utils.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerformanceActivity extends AppCompatActivity {

    private TextInputEditText phoneNumberInput;
    private MaterialButton fetchButton;
    private RecyclerView performanceRecyclerView;
    private TextInputLayout semesterInputLayout; // Обновляем на TextInputLayout
    private AutoCompleteTextView semesterSpinner;
    private PerformanceAdapter performanceAdapter;
    private ProgressBar loadingProgressBar;
    private List<PerformanceResponse.Plan> allPlans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_performance);

        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        fetchButton = findViewById(R.id.fetchButton);
        performanceRecyclerView = findViewById(R.id.performanceRecyclerView);
        semesterInputLayout = findViewById(R.id.semester_input_layout); // Инициализируем TextInputLayout
        semesterSpinner = findViewById(R.id.semesterSpinner);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // Скрываем весь блок semesterInputLayout по умолчанию
        semesterInputLayout.setVisibility(View.GONE);

        performanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        PreferenceManager preferenceManager = new PreferenceManager(this);
        String savedPhoneNumber = preferenceManager.getPhoneNumber();

        if (savedPhoneNumber != null && !savedPhoneNumber.isEmpty()) {
            phoneNumberInput.setText(savedPhoneNumber);
        }

        fetchButton.setOnClickListener(v -> fetchPerformanceData());

        // Настройка AutoCompleteTextView для выбора семестра
        semesterSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSemester = (String) parent.getItemAtPosition(position);
            filterBySemester(allPlans, selectedSemester);
        });

        // Форматирование номера телефона
        phoneNumberInput.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private int cursorPosition;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cursorPosition = phoneNumberInput.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;

                String raw = s.toString().replaceAll("[^\\d]", "");
                if (raw.startsWith("7")) {
                    raw = raw.substring(1);
                }
                if (raw.length() > 10) {
                    raw = raw.substring(0, 10);
                }

                StringBuilder formatted = new StringBuilder();
                formatted.append("+7 ");
                if (raw.length() > 0) {
                    formatted.append("(").append(raw.substring(0, Math.min(3, raw.length())));
                }
                if (raw.length() >= 4) {
                    formatted.append(") ").append(raw.substring(3, Math.min(6, raw.length())));
                }
                if (raw.length() >= 7) {
                    formatted.append("-").append(raw.substring(6, Math.min(8, raw.length())));
                }
                if (raw.length() >= 9) {
                    formatted.append("-").append(raw.substring(8, Math.min(10, raw.length())));
                }

                phoneNumberInput.removeTextChangedListener(this);
                phoneNumberInput.setText(formatted.toString());
                phoneNumberInput.setSelection(phoneNumberInput.getText().length());
                phoneNumberInput.addTextChangedListener(this);

                preferenceManager.setPhoneNumber(formatted.toString());
                isFormatting = false;
            }
        });
    }

    private void fetchPerformanceData() {

        showProgressBar();

        String rawPhone = phoneNumberInput.getText().toString().replaceAll("[^\\d]", ""); // убираем все символы кроме цифр

        // Убираем лишнюю семёрку в начале, если она есть
        if (rawPhone.startsWith("7") && rawPhone.length() == 11) {
            rawPhone = rawPhone.substring(1); // Оставляем только последние 10 цифр
        }

        if (rawPhone.length() != 10) {
            Toast.makeText(this, "Введите корректный номер (10 цифр)", Toast.LENGTH_SHORT).show();
            hideProgressBar();
            return;
        }

        PerformanceApi apiService = ApiClient.getRetrofitInstance().create(PerformanceApi.class);
        apiService.getPerformance(rawPhone).enqueue(new Callback<PerformanceResponse>() {
            @Override
            public void onResponse(Call<PerformanceResponse> call, Response<PerformanceResponse> response) {
                hideProgressBar();
                if (response.isSuccessful()) {
                    PerformanceResponse performance = response.body();
                    allPlans = performance.getPlans();
                    updateRecyclerView(performance);
                    setupSemesterSpinner(allPlans);
                    // Показываем semesterInputLayout только после успешного получения данных
                    semesterInputLayout.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(PerformanceActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    semesterInputLayout.setVisibility(View.GONE); // Скрываем при ошибке
                }
            }

            @Override
            public void onFailure(Call<PerformanceResponse> call, Throwable t) {
                hideProgressBar();
                Toast.makeText(PerformanceActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                semesterInputLayout.setVisibility(View.GONE); // Скрываем при ошибке сети
            }
        });
    }

    private void updateRecyclerView(PerformanceResponse performance) {
        if (performance != null && performance.getPlans() != null) {
            List<PerformanceResponse.Plan.Period.PlanCell> planCells = new ArrayList<>();
            for (PerformanceResponse.Plan plan : performance.getPlans()) {
                for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
                    planCells.addAll(getPlanCellsFromPeriod(period));
                }
            }

            if (planCells.isEmpty()) {
                // If no plan cells found, show the "Нет данных" message
                performanceRecyclerView.setVisibility(View.GONE);
                TextView noDataTextView = findViewById(R.id.tvNoData);
                noDataTextView.setVisibility(View.VISIBLE);
            } else {
                // If data is available, update the RecyclerView
                performanceRecyclerView.setVisibility(View.VISIBLE);
                TextView noDataTextView = findViewById(R.id.tvNoData);
                noDataTextView.setVisibility(View.GONE);

                performanceAdapter = new PerformanceAdapter(this, planCells);
                performanceRecyclerView.setAdapter(performanceAdapter);
            }
        }
    }

    private void filterBySemester(List<PerformanceResponse.Plan> allPlans, String semesterName) {
        List<PerformanceResponse.Plan.Period.PlanCell> filteredPlanCells = new ArrayList<>();
        for (PerformanceResponse.Plan plan : allPlans) {
            for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
                if (period.getName().equals(semesterName)) {
                    filteredPlanCells.addAll(getPlanCellsFromPeriod(period));
                }
            }
        }

        if (filteredPlanCells.isEmpty()) {
            // If no data found for the selected semester, show "Нет данных"
            performanceRecyclerView.setVisibility(View.GONE);
            TextView noDataTextView = findViewById(R.id.tvNoData);
            noDataTextView.setVisibility(View.VISIBLE);
        } else {
            performanceRecyclerView.setVisibility(View.VISIBLE);
            TextView noDataTextView = findViewById(R.id.tvNoData);
            noDataTextView.setVisibility(View.GONE);

            performanceAdapter = new PerformanceAdapter(this, filteredPlanCells);
            performanceRecyclerView.setAdapter(performanceAdapter);
        }
    }

    private List<PerformanceResponse.Plan.Period.PlanCell> getPlanCellsFromPeriod(PerformanceResponse.Plan.Period period) {
        List<PerformanceResponse.Plan.Period.PlanCell> planCells = new ArrayList<>();
        for (PerformanceResponse.Plan.Period.PlanCell planCell : period.getPlanCells()) {
            planCells.add(planCell);
        }
        return planCells;
    }

    private void setupSemesterSpinner(List<PerformanceResponse.Plan> allPlans) {
        List<String> semesterNames = getSemesterNames(allPlans);

        if (semesterNames.isEmpty()) {
            return;
        }

        semesterNames.sort(String::compareTo);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, semesterNames);
        semesterSpinner.setAdapter(semesterAdapter);

        // Выбираем последний семестр по умолчанию и вызываем фильтрацию
        String defaultSemester = semesterNames.get(semesterNames.size() - 1);
        semesterSpinner.setText(defaultSemester, false);
        filterBySemester(allPlans, defaultSemester);

        semesterSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSemester = (String) parent.getItemAtPosition(position);
            filterBySemester(allPlans, selectedSemester);
        });
    }

    private List<String> getSemesterNames(List<PerformanceResponse.Plan> plans) {
        Set<String> semesterNames = new HashSet<>();
        for (PerformanceResponse.Plan plan : plans) {
            for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
                semesterNames.add(period.getName());
            }
        }
        return new ArrayList<>(semesterNames);
    }

    private void showProgressBar() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(200); // Длительность анимации
        loadingProgressBar.startAnimation(fadeIn);
    }

    private void hideProgressBar() {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(200); // Длительность анимации
        loadingProgressBar.startAnimation(fadeOut);
        loadingProgressBar.setVisibility(View.GONE);
    }
}