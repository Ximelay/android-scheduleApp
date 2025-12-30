package com.example.irkpo_management;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.irkpo_management.utils.DataProvider;
import com.example.irkpo_management.utils.GroupUtils;
import com.example.irkpo_management.utils.PreferenceManager;
import com.example.irkpo_management.utils.TeacherUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoritesManagementActivity extends AppCompatActivity {

    private static final String TAG = "FavoritesManagement";

    private RadioGroup radioGroupType;
    private RadioButton radioGroupOption;
    private RadioButton radioTeacherOption;
    private MaterialCardView cardGroupSelection;
    private MaterialCardView cardTeacherSelection;
    private AutoCompleteTextView etSearchGroup;
    private AutoCompleteTextView etSearchTeacher;
    private TextView tvCurrentFavoriteGroup;
    private TextView tvCurrentFavoriteTeacher;
    private Button btnSaveGroup;
    private Button btnSaveTeacher;
    private Button btnClearGroup;
    private Button btnClearTeacher;

    private PreferenceManager preferenceManager;
    private ArrayAdapter<String> groupAdapter;
    private ArrayAdapter<String> teacherAdapter;
    private List<String> groupList;
    private List<String> teacherList;

    private boolean groupsLoaded = false;
    private boolean teachersLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_favorites_management);

        preferenceManager = new PreferenceManager(this);

        initializeViews();
        setupListeners();
        loadData();
        updateCurrentFavorites();
    }

    private void initializeViews() {
        radioGroupType = findViewById(R.id.radioGroupType);
        radioGroupOption = findViewById(R.id.radioGroupOption);
        radioTeacherOption = findViewById(R.id.radioTeacherOption);
        cardGroupSelection = findViewById(R.id.cardGroupSelection);
        cardTeacherSelection = findViewById(R.id.cardTeacherSelection);
        etSearchGroup = findViewById(R.id.etSearchGroup);
        etSearchTeacher = findViewById(R.id.etSearchTeacher);
        tvCurrentFavoriteGroup = findViewById(R.id.tvCurrentFavoriteGroup);
        tvCurrentFavoriteTeacher = findViewById(R.id.tvCurrentFavoriteTeacher);
        btnSaveGroup = findViewById(R.id.btnSaveGroup);
        btnSaveTeacher = findViewById(R.id.btnSaveTeacher);
        btnClearGroup = findViewById(R.id.btnClearGroup);
        btnClearTeacher = findViewById(R.id.btnClearTeacher);

        groupList = new ArrayList<>();
        teacherList = new ArrayList<>();

        groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, groupList);
        etSearchGroup.setAdapter(groupAdapter);
        etSearchGroup.setThreshold(1);

        teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, teacherList);
        etSearchTeacher.setAdapter(teacherAdapter);
        etSearchTeacher.setThreshold(1);

        // Устанавливаем начальное состояние видимости карточек
        String lastSelectionType = preferenceManager.getLastSelectionType();
        if (PreferenceManager.TYPE_GROUP.equals(lastSelectionType)) {
            radioGroupOption.setChecked(true);
            showGroupCard();
        } else {
            radioTeacherOption.setChecked(true);
            showTeacherCard();
        }
    }

    private void setupListeners() {
        radioGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioGroupOption) {
                showGroupCard();
            } else if (checkedId == R.id.radioTeacherOption) {
                showTeacherCard();
            }
        });

        btnSaveGroup.setOnClickListener(v -> saveGroupFavorite());
        btnSaveTeacher.setOnClickListener(v -> saveTeacherFavorite());
        btnClearGroup.setOnClickListener(v -> clearGroupFavorite());
        btnClearTeacher.setOnClickListener(v -> clearTeacherFavorite());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void showGroupCard() {
        cardGroupSelection.setVisibility(View.VISIBLE);
        cardTeacherSelection.setVisibility(View.GONE);
    }

    private void showTeacherCard() {
        cardGroupSelection.setVisibility(View.GONE);
        cardTeacherSelection.setVisibility(View.VISIBLE);
    }

    private void loadData() {
        // Загрузка групп
        GroupUtils.init(this, new DataProvider.LoadCallback<Integer>() {
            @Override
            public void onSuccess(Map<String, Integer> map) {
                Log.d(TAG, "Groups loaded successfully");
                groupList.clear();
                groupList.addAll(map.keySet());
                groupAdapter.notifyDataSetChanged();
                groupsLoaded = true;
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Failed to load groups", t);
                Toast.makeText(FavoritesManagementActivity.this,
                    "Ошибка загрузки групп", Toast.LENGTH_SHORT).show();
            }
        });

        // Загрузка преподавателей
        TeacherUtils.init(this, new DataProvider.LoadCallback<String>() {
            @Override
            public void onSuccess(Map<String, String> map) {
                Log.d(TAG, "Teachers loaded successfully");
                teacherList.clear();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String formattedName = formatTeacherName(entry.getKey());
                    teacherList.add(formattedName);
                }
                teacherAdapter.notifyDataSetChanged();
                teachersLoaded = true;
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Failed to load teachers", t);
                Toast.makeText(FavoritesManagementActivity.this,
                    "Ошибка загрузки преподавателей", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCurrentFavorites() {
        String defaultGroup = preferenceManager.getDefaultGroup();
        if (defaultGroup != null && !defaultGroup.isEmpty()) {
            tvCurrentFavoriteGroup.setText("Текущее избранное: " + defaultGroup);
            btnClearGroup.setVisibility(View.VISIBLE);
        } else {
            tvCurrentFavoriteGroup.setText("Избранная группа не выбрана");
            btnClearGroup.setVisibility(View.GONE);
        }

        String defaultTeacher = preferenceManager.getDefaultTeacher();
        if (defaultTeacher != null && !defaultTeacher.isEmpty()) {
            tvCurrentFavoriteTeacher.setText("Текущее избранное: " + defaultTeacher);
            btnClearTeacher.setVisibility(View.VISIBLE);
        } else {
            tvCurrentFavoriteTeacher.setText("Избранный преподаватель не выбран");
            btnClearTeacher.setVisibility(View.GONE);
        }
    }

    private void saveGroupFavorite() {
        String groupName = etSearchGroup.getText().toString().trim();
        if (groupName.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, выберите группу", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer groupId = GroupUtils.getGroupId(groupName);
        if (groupId == null) {
            Toast.makeText(this, "Группа не найдена", Toast.LENGTH_SHORT).show();
            return;
        }

        preferenceManager.setDefaultGroup(groupName);
        preferenceManager.setGroupId(groupId);
        preferenceManager.setLastSelectionType(PreferenceManager.TYPE_GROUP);

        updateCurrentFavorites();
        Toast.makeText(this, "Группа " + groupName + " добавлена в избранное", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Saved group favorite: " + groupName + " (ID: " + groupId + ")");
    }

    private void saveTeacherFavorite() {
        String teacherName = etSearchTeacher.getText().toString().trim();
        if (teacherName.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, выберите преподавателя", Toast.LENGTH_SHORT).show();
            return;
        }

        String teacherId = findTeacherIdByFormattedName(teacherName);
        if (teacherId == null) {
            Toast.makeText(this, "Преподаватель не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        preferenceManager.setDefaultTeacher(teacherName);
        preferenceManager.setTeacherId(teacherId);
        preferenceManager.setLastSelectionType(PreferenceManager.TYPE_TEACHER);

        updateCurrentFavorites();
        Toast.makeText(this, "Преподаватель " + teacherName + " добавлен в избранное", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Saved teacher favorite: " + teacherName + " (ID: " + teacherId + ")");
    }

    private void clearGroupFavorite() {
        preferenceManager.setDefaultGroup(null);
        preferenceManager.setGroupId(-1);
        etSearchGroup.setText("");
        updateCurrentFavorites();
        Toast.makeText(this, "Избранная группа удалена", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Cleared group favorite");
    }

    private void clearTeacherFavorite() {
        preferenceManager.setDefaultTeacher(null);
        preferenceManager.setTeacherId(null);
        etSearchTeacher.setText("");
        updateCurrentFavorites();
        Toast.makeText(this, "Избранный преподаватель удалён", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Cleared teacher favorite");
    }

    private String formatTeacherName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return fullName;
        }

        String[] parts = fullName.trim().split("\\s+");
        if (parts.length < 2) {
            return fullName;
        }

        StringBuilder formatted = new StringBuilder();
        formatted.append(parts[0]); // Фамилия

        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                formatted.append(" ").append(parts[i].charAt(0)).append(".");
            }
        }

        return formatted.toString();
    }

    private String findTeacherIdByFormattedName(String formattedName) {
        Map<String, String> teachersMap = TeacherUtils.getTeachersMap();
        if (teachersMap == null) {
            return null;
        }

        for (Map.Entry<String, String> entry : teachersMap.entrySet()) {
            String formatted = formatTeacherName(entry.getKey());
            if (formatted.equals(formattedName)) {
                return entry.getValue();
            }
        }
        return null;
    }
}

