package com.example.sheduleapp_v5.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sheduleapp_v5.R;
import com.example.sheduleapp_v5.models.DaySchedule;
import com.example.sheduleapp_v5.models.LessonIndex;
import com.example.sheduleapp_v5.models.LessonItem;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<DaySchedule> daySchedules;

    public ScheduleAdapter(List<DaySchedule> daySchedules) {
        this.daySchedules = daySchedules;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DaySchedule daySchedule = daySchedules.get(position);

        // Отображаем день недели
        holder.tvDayOfWeek.setText(daySchedule.getDayOfWeek());

        // Отображаем тип недели
        String weekType = (daySchedule.getWeekType() == 1) ? "Круглая" : "Квадратная";
        holder.tvWeekType.setText("Тип недели: " + weekType);

        // Строка для всех уроков в день
        StringBuilder lessonDetails = new StringBuilder();

        // Перебираем все уроки для дня
        for (LessonIndex lessonIndex : daySchedule.getLessonIndexes()) {
            // Перебираем все предметы (LessonItem) в этом индексе
            for (LessonItem lessonItem : lessonIndex.getItems()) {
                // Добавляем подробности урока
                lessonDetails.append(lessonItem.getLessonName()).append("\n")
                        .append("Время: ").append(lessonIndex.getLessonStartTime()).append(" - ").append(lessonIndex.getLessonEndTime()).append("\n")
                        .append("Преподаватель: ").append(lessonItem.getTeacherName()).append("\n")
                        .append("Аудитория: ").append(lessonItem.getClassroom()).append("\n\n");
            }
        }
        // Устанавливаем строку уроков
        holder.tvLessonName.setText(lessonDetails.toString());
    }

    @Override
    public int getItemCount() {
        return daySchedules.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek, tvWeekType, tvLessonName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            tvWeekType = itemView.findViewById(R.id.tvWeekType);
            tvLessonName = itemView.findViewById(R.id.tvLessonName);
        }
    }
}
