package com.example.sheduleapp_v5.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sheduleapp_v5.R;
import com.example.sheduleapp_v5.models.LessonItem;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<LessonItem> lessonItems;

    public ScheduleAdapter(List<LessonItem> lessonItems) {
        this.lessonItems = lessonItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LessonItem item = lessonItems.get(position);
        holder.tvLessonName.setText(item.getLessonName());
        holder.tvTime.setText(item.getClassroom());
        holder.tvTeacher.setText(item.getTeacherName());
    }

    @Override
    public int getItemCount() {
        return lessonItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLessonName, tvTime, tvTeacher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLessonName = itemView.findViewById(R.id.tvLessonName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
        }
    }
}
