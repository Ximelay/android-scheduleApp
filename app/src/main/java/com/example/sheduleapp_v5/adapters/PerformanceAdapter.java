package com.example.sheduleapp_v5.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sheduleapp_v5.R;
import com.example.sheduleapp_v5.models.PerformanceResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PerformanceAdapter extends RecyclerView.Adapter<PerformanceAdapter.PerformanceViewHolder> {
    private List<PerformanceResponse.Plan.Period.PlanCell> planCells;
    private Context context;

    public PerformanceAdapter(Context context, List<PerformanceResponse.Plan.Period.PlanCell> planCells) {
        this.context = context;
        this.planCells = planCells;
    }

    @NonNull
    @Override
    public PerformanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.performance_item, parent, false);
        return new PerformanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerformanceViewHolder holder, int position) {
        PerformanceResponse.Plan.Period.PlanCell planCell = planCells.get(position);

        // Название предмета
        holder.subjectName.setText(planCell.getRowName());

        double percentage = 0;
        if (planCell.getSheets() != null && !planCell.getSheets().isEmpty()) {
            List<PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson> lessons = planCell.getSheets().get(0).getLessons();
            percentage = calculatePerformancePercentage(lessons);
        }
        holder.percentageTextView.setText(String.format("%.2f%%", percentage));

        // Обработка всех преподавателей
        String teacherNames = getTeacherNames(planCell.getSheets());
        holder.subjectCodeTextView.setText(teacherNames.isEmpty() ? "Неизвестен" : teacherNames);

        // Логика для аттестации: проверяем все листы на наличие аттестации
        String attestationText;
        boolean isAttested = false;
        if (planCell.getSheets() != null && !planCell.getSheets().isEmpty()) {
            for (PerformanceResponse.Plan.Period.PlanCell.Sheet sheet : planCell.getSheets()) {
                String currentAttestation = sheet.getCurrentAttestationMarkName();
                String sheetAttestation = sheet.getSheetAttestationMarkName();
                if ((currentAttestation != null && !currentAttestation.isEmpty()) ||
                        (sheetAttestation != null && !sheetAttestation.isEmpty())) {
                    isAttested = true;
                    break; // Выходим из цикла, как только находим аттестацию
                }
            }
            String finalMark = planCell.getAttestation() != null ? planCell.getAttestation().getMarkName() : null;
            attestationText = "Итог: " + (finalMark != null && !finalMark.isEmpty() ? finalMark : "-");
        } else {
            attestationText = "Итог: отсутствует";
        }

        holder.attendanceText.setText(attestationText);
        holder.attestationStatus.setText(isAttested ? "Аттестован ✔️" : "Не аттестован ❌");

        holder.itemView.setOnClickListener(v -> showSubjectDetailDialog(planCell));
    }

    private String getTeacherNames(List<PerformanceResponse.Plan.Period.PlanCell.Sheet> sheets) {
        if (sheets == null || sheets.isEmpty()) return "";

        StringBuilder teacherNames = new StringBuilder();
        Set<String> uniqueTeachers = new HashSet<>();

        for (PerformanceResponse.Plan.Period.PlanCell.Sheet sheet : sheets) {
            String teacherName = sheet.getTeacherName();
            if (teacherName != null && !teacherName.isEmpty() && !uniqueTeachers.contains(teacherName)) {
                if (teacherNames.length() > 0) teacherNames.append(", ");
                teacherNames.append(teacherName);
                uniqueTeachers.add(teacherName);
            }
        }

        return teacherNames.toString();
    }

    private String formatDate(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");

        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }

    @Override
    public int getItemCount() {
        return planCells.size();
    }

    private void showSubjectDetailDialog(PerformanceResponse.Plan.Period.PlanCell planCell) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_subject_detail, null);

        TextView subjectName = dialogView.findViewById(R.id.subjectNameTextView);
        TextView teacherName = dialogView.findViewById(R.id.teacherNameTextView);
        TextView attestation = dialogView.findViewById(R.id.attestationTextView);
        LinearLayout lessonsContainer = dialogView.findViewById(R.id.lessonsContainer);

        subjectName.setText(planCell.getRowIndex() + " " + planCell.getRowName());
        teacherName.setText("Преподаватели: " + getTeacherNames(planCell.getSheets()));

        // Логика аттестации
        String currentAttestation = planCell.getSheets() != null && !planCell.getSheets().isEmpty()
                ? planCell.getSheets().get(0).getCurrentAttestationMarkName() : null;
        String finalMark = planCell.getAttestation() != null ? planCell.getAttestation().getMarkName() : null;
        attestation.setText("Текущая аттестация: " + (currentAttestation != null ? currentAttestation : "-") +
                " | Итог: " + (finalMark != null ? finalMark : "-"));

        // Сортировка уроков: сначала практические, потом лекционные
        List<PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson> practicalLessons = new ArrayList<>();
        List<PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson> lectureLessons = new ArrayList<>();

        for (PerformanceResponse.Plan.Period.PlanCell.Sheet sheet : planCell.getSheets()) {
            if (sheet.getLessons() == null) continue;
            for (PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson : sheet.getLessons()) {
                String lessonType = lesson.getLessonTypeName() != null ? lesson.getLessonTypeName() : "";
                if (lessonType.contains("Практическое занятие")) {
                    practicalLessons.add(lesson);
                } else if (lessonType.contains("Лекционное занятие")) {
                    lectureLessons.add(lesson);
                }
            }
        }

        // Добавление заголовка и уроков для практических занятий
        if (!practicalLessons.isEmpty()) {
            View headerView = inflater.inflate(R.layout.section_header, lessonsContainer, false);
            TextView practicalHeader = headerView.findViewById(R.id.sectionTitleTextView);
            ImageView practicalArrow = headerView.findViewById(R.id.sectionArrowImageView);
            practicalHeader.setText("Практические занятия");
            lessonsContainer.addView(headerView);

            LinearLayout practicalLayout = new LinearLayout(context);
            practicalLayout.setOrientation(LinearLayout.VERTICAL);
            practicalLayout.setVisibility(View.VISIBLE);
            for (PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson : practicalLessons) {
                addLessonCard(lesson, practicalLayout);
            }
            lessonsContainer.addView(practicalLayout);

            headerView.setOnClickListener(v -> toggleLessons(practicalArrow, practicalLayout));
        }

        // Добавление заголовка и уроков для лекционных занятий
        if (!lectureLessons.isEmpty()) {
            View headerView = inflater.inflate(R.layout.section_header, lessonsContainer, false);
            TextView lectureHeader = headerView.findViewById(R.id.sectionTitleTextView);
            ImageView lectureArrow = headerView.findViewById(R.id.sectionArrowImageView);
            lectureHeader.setText("Лекционные занятия");
            lessonsContainer.addView(headerView);

            LinearLayout lectureLayout = new LinearLayout(context);
            lectureLayout.setOrientation(LinearLayout.VERTICAL);
            lectureLayout.setVisibility(View.VISIBLE);
            for (PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson : lectureLessons) {
                addLessonCard(lesson, lectureLayout);
            }
            lessonsContainer.addView(lectureLayout);

            headerView.setOnClickListener(v -> toggleLessons(lectureArrow, lectureLayout));
        }

        builder.setView(dialogView)
                .setPositiveButton("Закрыть", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void toggleLessons(ImageView arrow, LinearLayout lessonLayout) {
        if (lessonLayout.getVisibility() == View.VISIBLE) {
            lessonLayout.setVisibility(View.GONE);
            arrow.setRotation(0); // Стрелка вправо (0 градусов)
        } else {
            lessonLayout.setVisibility(View.VISIBLE);
            arrow.setRotation(90); // Стрелка вниз (90 градусов)
        }

        // Анимация для плавного скрытия/показа
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(200);
        lessonLayout.startAnimation(animation);
    }

    private void addLessonCard(PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson, LinearLayout lessonsLayout) {
        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(24, 24, 24, 24);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(16);
        cardView.setCardElevation(8);
        cardView.setUseCompatPadding(true);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(24, 24, 24, 24);

        TextView lessonView = new TextView(context);
        lessonView.setTextSize(16);
        String lessonDate = lesson.getLessonDate();
        lessonView.setText("📅 " + formatDate(lessonDate) + "\n" + "📚 " + lesson.getThemePlanName());

        TextView markView = new TextView(context);
        markView.setTextSize(16);
        String mark = lesson.getMarkName() != null ? lesson.getMarkName() : "-";
        markView.setText("📝 Оценка: " + mark);

        if (mark.equals("2") || mark.toLowerCase().contains("незачет")) {
            markView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else if (mark.equals("5") || mark.equals("4") || mark.toLowerCase().contains("зачет")) {
            markView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            markView.setTextColor(context.getResources().getColor(android.R.color.black));
        }

        linearLayout.addView(lessonView);
        linearLayout.addView(markView);
        cardView.addView(linearLayout);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(300);
        cardView.startAnimation(animation);
        lessonsLayout.addView(cardView);
    }

    private double calculatePerformancePercentage(List<PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson> lessons) {
        int totalCount = 0;
        int attendedCount = 0;

        for (PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson : lessons) {
            String markName = lesson.getMarkName();

            if ("НУ".equalsIgnoreCase(markName)) {
                continue;
            }

            totalCount++;

            if (markName == null || !"Н".equalsIgnoreCase(markName)) {
                attendedCount++;
            }
        }

        return (totalCount == 0) ? 0 : (double) attendedCount / totalCount * 100;
    }

    public static class PerformanceViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName, attendanceText, attestationStatus, subjectCodeTextView, percentageTextView;

        public PerformanceViewHolder(View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subjectNameTextView);
            attendanceText = itemView.findViewById(R.id.attendanceTextView);
            attestationStatus = itemView.findViewById(R.id.attestationStatusTextView);
            subjectCodeTextView = itemView.findViewById(R.id.subjectCodeTextView);
            percentageTextView = itemView.findViewById(R.id.percentageTextView);
        }
    }
}