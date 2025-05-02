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

        holder.tvDayOfWeek.setText(daySchedule.getDayOfWeek());

        StringBuilder lessonDetails = new StringBuilder();

        for (LessonIndex lessonIndex : daySchedule.getLessonIndexes()) {
            for (LessonItem lessonItem : lessonIndex.getItems()) {
                lessonDetails.append(lessonItem.getLessonName()).append("\n")
                        .append("Время: ").append(lessonIndex.getLessonStartTime()).append(" - ").append(lessonIndex.getLessonEndTime()).append("\n")
                        .append("Преподаватель: ").append(lessonItem.getTeacherName()).append("\n")
                        .append("Аудитория: ").append(lessonItem.getClassroom() != null ? lessonItem.getClassroom() : "—")
                        .append(lessonItem.getLocation() != null ? " (" + lessonItem.getLocation() + ")" : "").append("\n\n");
            }
        }

        holder.tvLessonName.setText(lessonDetails.toString().trim());
    }

    @Override
    public int getItemCount() {
        return daySchedules.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek, tvLessonName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            tvLessonName = itemView.findViewById(R.id.tvLessonName);
        }
    }
}
