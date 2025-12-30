package com.example.irkpo_management;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {

    // Кнопки
    MaterialButton buttonSchedule;
    MaterialButton buttonPerformance;
    MaterialButton buttonMoodle;
    MaterialButton buttonAbout;
    MaterialButton buttonExport;
    MaterialButton buttonFavorites;
    SwitchMaterial themeSwitch;
    private SharedPreferences sharedPreferences;
    private static final String TELEGRAM = BuildConfig.TELEGRAM_NIKNEIM;
    private static final String GITHUB = BuildConfig.GITHUB_NIKNEIM;
    private static final String GITHUB_REPOSITORY = BuildConfig.GITHUB_REPOSITORY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);


        buttonSchedule = findViewById(R.id.button_schedule);
        buttonPerformance = findViewById(R.id.button_performance);
        buttonMoodle = findViewById(R.id.button_moodle);
        themeSwitch = findViewById(R.id.theme_switch);
        buttonAbout = findViewById(R.id.button_about);
        buttonExport = findViewById(R.id.button_export);
        buttonFavorites = findViewById(R.id.button_favorites);

        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("darkMode", false);
        themeSwitch.setChecked(isDarkMode);

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("darkMode", isChecked);
                editor.apply();

                if (isChecked) {
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
                intent.setPackage("com.moodle.moodlemobile");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://irkpo.ru/moodle/")));
                }
            }
        });

        buttonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("О программе");

                String versionName = "Неизвестно";
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    versionName = pInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String aboutMessage = "Автор: " + GITHUB + "\n" +
                        "Версия: " + versionName + "\n" +
                        "Приложенение создано для студентов и преподавателей ИРКПО\n" +
                        "Предложения по улучшению можете писать в Telegram: " + TELEGRAM;

                SpannableString spannableMessage = new SpannableString(aboutMessage);

                int githubStart = aboutMessage.indexOf(GITHUB);
                int githubEnd = githubStart + GITHUB.length();
                ClickableSpan githubLink = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        try {
                            Intent githubIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/" + GITHUB));
                            startActivity(githubIntent);
                        } catch (ActivityNotFoundException e) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/" + GITHUB));
                            startActivity(browserIntent);
                        }
                    }
                };
                spannableMessage.setSpan(githubLink, githubStart, githubEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                int telegramStart = aboutMessage.indexOf(TELEGRAM);
                int telegramEnd = telegramStart + TELEGRAM.length();
                ClickableSpan telegramLink = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        try {
                            Intent telegramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=" + TELEGRAM));
                            startActivity(telegramIntent);
                        } catch (ActivityNotFoundException e) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/" + TELEGRAM));
                            startActivity(browserIntent);
                        }
                    }
                };
                spannableMessage.setSpan(telegramLink, telegramStart, telegramEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                TextView messageTextView = new TextView(MainActivity.this);
                messageTextView.setText(spannableMessage);
                messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
                messageTextView.setPadding(40, 20, 40, 20); // Отступы для текста

                builder.setView(messageTextView);

                builder.setPositiveButton("Перейти к исходному коду", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_REPOSITORY));
                    startActivity(intent);
                });

                builder.setNegativeButton("Закрыть", (dialog, which) -> dialog.dismiss());

                builder.show();
            }
        });

        buttonExport.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExportActivity.class);
            startActivity(intent);
        });

        buttonFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesManagementActivity.class);
            startActivity(intent);
        });
    }
}