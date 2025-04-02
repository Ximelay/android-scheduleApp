package com.example.sheduleapp_v5.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sheduleapp_v5.DisplayLessonItem;
import com.example.sheduleapp_v5.R;
import com.example.sheduleapp_v5.models.LessonItem;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {

    private List<DisplayLessonItem> lessonList;

    public LessonAdapter(List<DisplayLessonItem> lessonList) {
        this.lessonList = lessonList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisplayLessonItem item = lessonList.get(position);

        holder.tvDay.setText(item.getDayOfWeek());
        holder.tvDay.setVisibility(item.isFirstOfDay() ? View.VISIBLE : View.GONE);

        holder.tvTime.setText(item.getStartTime() + " - " + item.getEndTime());

        StringBuilder builder = new StringBuilder();
        for (LessonItem lesson : item.getLessons()) {
            builder.append("Предмет: ").append(lesson.getLessonName() != null ? lesson.getLessonName() : "—").append("\n")
                    .append("Преподаватель: ").append(lesson.getTeacherName() != null ? lesson.getTeacherName() : "—").append("\n")
                    .append("Аудитория: ").append(lesson.getClassroom() != null ? lesson.getClassroom() : "—");

            boolean hasExtras = lesson.getComment() != null || lesson.getSubgroup() != null || lesson.getWeekType() != null;
            if (hasExtras) {
                builder.append("\n");

                // Значок текущей недели
                if (lesson.getWeekType() != null && lesson.getWeekType().equals(item.getCurrentWeekType())) {
                    if (lesson.getWeekType() == 1) {
                        builder.append("🟢 ");
                    } else if (lesson.getWeekType() == 2) {
                        builder.append("🔺 ");
                    }
                }

                builder.append("⚙️ ");

                if (lesson.getSubgroup() != null) {
                    builder.append("подгр. ").append(lesson.getSubgroup()).append(" ");
                }

                if (lesson.getComment() != null) {
                    builder.append(lesson.getComment()).append(" ");
                }
            }

            builder.append("\n\n");
        }

        holder.tvDetails.setText(builder.toString().trim());

        // Отображаем XML-иконки недель
        int currentWeekType = item.getCurrentWeekType();
        boolean hasWeek1 = false;
        boolean hasWeek2 = false;

        for (LessonItem lesson : item.getLessons()) {
            if (lesson.getWeekType() != null) {
                if (lesson.getWeekType() == 1) hasWeek1 = true;
                if (lesson.getWeekType() == 2) hasWeek2 = true;
            }
        }

        // Круглая неделя
        if (hasWeek1) {
            holder.iconCircle.setVisibility(View.VISIBLE);
            if (currentWeekType == 1) {
                holder.iconCircle.setImageResource(R.drawable.ic_circle_filled);
            } else {
                holder.iconCircle.setImageResource(R.drawable.ic_circle_outline);
            }
        } else {
            holder.iconCircle.setVisibility(View.GONE);
        }

        // Треугольная неделя
        if (hasWeek2) {
            holder.iconTriangle.setVisibility(View.VISIBLE);
            if (currentWeekType == 2) {
                holder.iconTriangle.setImageResource(R.drawable.ic_triangle_filled);
            } else {
                holder.iconTriangle.setImageResource(R.drawable.ic_triangle_outline);
            }
        } else {
            holder.iconTriangle.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvTime, tvDetails;
        ImageView iconCircle, iconTriangle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            iconCircle = itemView.findViewById(R.id.iconCircle);
            iconTriangle = itemView.findViewById(R.id.iconTriangle);
        }
    }
}
