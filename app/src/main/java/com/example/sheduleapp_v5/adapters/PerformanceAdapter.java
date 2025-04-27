package com.example.sheduleapp_v5.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sheduleapp_v5.R;
import com.example.sheduleapp_v5.models.PerformanceResponse;

import java.util.List;

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

        holder.subjectCodeTextView.setText(planCell.getSheets().get(0).getTeacherName());

        // Проверка итоговой оценки
        String attestationText;
        boolean isAttested = false;
        if (planCell.getAttestation() != null && planCell.getAttestation().getMarkName() != null) {
            attestationText = "Итог: " + planCell.getAttestation().getMarkName();
            isAttested = true;
        } else {
            attestationText = "Итог: отсутствует";
        }

        // Устанавливаем текст оценки
        holder.attendanceText.setText(attestationText);

        // Новый элемент: статус аттестации
        holder.attestationStatus.setText(isAttested ? "Аттестован ✔️" : "Не аттестован ❌");

        holder.itemView.setOnClickListener(v -> showSubjectDetailDialog(planCell));
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
        teacherName.setText("Преподаватель: " + planCell.getSheets().get(0).getTeacherName());
        attestation.setText("Текущая аттестация: " + (planCell.getAttestation() != null ? planCell.getAttestation().getMarkName() : "-"));

        for (PerformanceResponse.Plan.Period.PlanCell.Sheet sheet : planCell.getSheets()) {
            for (PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson : sheet.getLessons()) {

                // Создаем CardView для урока
                androidx.cardview.widget.CardView cardView = new androidx.cardview.widget.CardView(context);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                cardParams.setMargins(24, 24, 24, 24); // Было 16 - стало 24 для воздуха
                cardView.setLayoutParams(cardParams);
                cardView.setRadius(16);
                cardView.setCardElevation(8);
                cardView.setUseCompatPadding(true);

                // Внутри CardView создаем TextView
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(24, 24, 24, 24);

                TextView lessonView = new TextView(context);
                lessonView.setTextSize(16);
                lessonView.setText(
                        "📅 " + lesson.getLessonDate() + "\n"
                                + "📚 " + lesson.getThemePlanName()
                );

                TextView markView = new TextView(context);
                markView.setTextSize(16);
                String mark = lesson.getMarkName() != null ? lesson.getMarkName() : "-";
                markView.setText("📝 Оценка: " + mark);

                // Раскраска оценки
                if (mark.equals("2") || mark.toLowerCase().contains("незачет")) {
                    markView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark)); // ❗️ Красный
                } else if (mark.equals("5") || mark.equals("4") || mark.toLowerCase().contains("зачет")) {
                    markView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark)); // ✅ Зеленый
                } else {
                    markView.setTextColor(context.getResources().getColor(android.R.color.black)); // Обычный
                }

                linearLayout.addView(lessonView);
                linearLayout.addView(markView);
                cardView.addView(linearLayout);
                Animation animation = new AlphaAnimation(0.0f, 1.0f);
                animation.setDuration(300);
                cardView.startAnimation(animation);
                lessonsContainer.addView(cardView);
            }
        }

        builder.setView(dialogView)
                .setPositiveButton("Закрыть", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static class PerformanceViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName, attendanceText, attestationStatus, subjectCodeTextView;

        public PerformanceViewHolder(View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subjectNameTextView);
            attendanceText = itemView.findViewById(R.id.attendanceTextView);
            attestationStatus = itemView.findViewById(R.id.attestationStatusTextView);
            subjectCodeTextView = itemView.findViewById(R.id.subjectCodeTextView);
        }
    }
}
