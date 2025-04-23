package com.example.sheduleapp_v5.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sheduleapp_v5.R;
import com.example.sheduleapp_v5.models.PerformanceResponse;

import java.util.List;

public class PerformanceAdapter extends RecyclerView.Adapter<PerformanceAdapter.PerformanceViewHolder> {
    private List<PerformanceResponse.Plan> plans;

    public PerformanceAdapter(List<PerformanceResponse.Plan> plans) {
        this.plans = plans;
    }

    @NonNull
    @Override
    public PerformanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.performance_item, parent, false);
        return new PerformanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerformanceViewHolder holder, int position) {
        PerformanceResponse.Plan plan = plans.get(position);
        holder.groupName.setText(plan.getGroupName());

        // Перебор периодов
        StringBuilder periods = new StringBuilder();
        for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
            periods.append(period.getName()).append("\n");
        }
        holder.subjectName.setText(periods.toString());

        // Перебор предметов
        StringBuilder subjects = new StringBuilder();
        for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
            for (PerformanceResponse.Plan.Period.PlanCell planCell : period.getPlanCells()) {
                subjects.append(planCell.getRowName()).append("\n");
            }
        }
        holder.attendanceText.setText(subjects.toString());
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public static class PerformanceViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, subjectName, attendanceText;

        public PerformanceViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupNameTextView);
            subjectName = itemView.findViewById(R.id.subjectNameTextView);
            attendanceText = itemView.findViewById(R.id.attendanceTextView);
        }
    }
}
