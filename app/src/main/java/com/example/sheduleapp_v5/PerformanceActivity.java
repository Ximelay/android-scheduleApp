package com.example.sheduleapp_v5;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sheduleapp_v5.adapters.PerformanceAdapter;
import com.example.sheduleapp_v5.models.PerformanceResponse;
import com.example.sheduleapp_v5.network.ApiClient;
import com.example.sheduleapp_v5.network.PerformanceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerformanceActivity extends AppCompatActivity {

    private EditText phoneNumberInput;
    private Button fetchButton;
    private RecyclerView performanceRecyclerView;
    private PerformanceAdapter performanceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);

        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        fetchButton = findViewById(R.id.fetchButton);
        performanceRecyclerView = findViewById(R.id.performanceRecyclerView);

        performanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchButton.setOnClickListener(v -> fetchPerformanceData());
    }

    private void fetchPerformanceData() {
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
            return;
        }

        PerformanceApi apiService = ApiClient.getRetrofitInstance().create(PerformanceApi.class);
        apiService.getPerformance(phoneNumber).enqueue(new Callback<PerformanceResponse>() {
            @Override
            public void onResponse(Call<PerformanceResponse> call, Response<PerformanceResponse> response) {
                if (response.isSuccessful()) {
                    PerformanceResponse performance = response.body();
                    updateRecyclerView(performance);
                } else {
                    Toast.makeText(PerformanceActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PerformanceResponse> call, Throwable t) {
                Toast.makeText(PerformanceActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecyclerView(PerformanceResponse performance) {
        if (performance != null && performance.getPlans() != null) {
            performanceAdapter = new PerformanceAdapter(performance.getPlans());
            performanceRecyclerView.setAdapter(performanceAdapter);
        }
    }
}
