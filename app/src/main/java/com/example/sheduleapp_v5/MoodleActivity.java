package com.example.sheduleapp_v5;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MoodleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_moodle);

        WebView webView = findViewById(R.id.moodle_webview);
        webView.getSettings().setJavaScriptEnabled(true); // Включите JavaScript для корректной работы Moodle
        webView.getSettings().setDomStorageEnabled(true); // Для хранения данных сессии
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url); // Обрабатывать все ссылки внутри WebView
                return true;
            }
        });
        webView.loadUrl("https://irkpo.ru/moodle/"); // Загрузка страницы Moodle

        // Обработка отступов для системных панелей
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}