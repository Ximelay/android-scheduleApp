package com.example.sheduleapp_v5;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.sheduleapp_v5.work.ReminderScheduler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {

    // Кнопки
    MaterialButton buttonSchedule;
    MaterialButton buttonPerformance;
    MaterialButton buttonMoodle;
    SwitchMaterial themeSwitch;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReminderScheduler.scheduleWeeklyCleanup(this);


        buttonSchedule = findViewById(R.id.button_schedule);
        buttonPerformance = findViewById(R.id.button_performance);
        buttonMoodle = findViewById(R.id.button_moodle);
        themeSwitch = findViewById(R.id.theme_switch);

        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("darkMode", false);
        themeSwitch.setChecked(isDarkMode);

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("darkMode", isChecked);
                editor.apply();

                if(isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                recreate();
            }
        });

        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

        buttonPerformance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PerformanceActivity.class);
                startActivity(intent);
            }
        });

        buttonMoodle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://irkpo.ru/moodle/"));
                intent.setPackage("com.moodle.moodlemobile"); // Пакет официального приложения Moodle
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Если приложение не установлено, открываем Moodle в браузере
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://irkpo.ru/moodle/")));
                }
            }
        });
    }
}