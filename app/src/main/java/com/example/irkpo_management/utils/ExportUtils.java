package com.example.irkpo_management.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.irkpo_management.ExportActivity;
import com.example.irkpo_management.export.ExportToExcelTask;
import com.example.irkpo_management.export.ExportToPdfTask;
import com.example.irkpo_management.models.PerformanceResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExportUtils {

    public static void exportToPdf(ExportActivity activity, List<PerformanceResponse.Plan> allPlans,
                                   boolean exportSubjects, boolean exportLessons, boolean exportTeachers, boolean exportAttestation) {
        if (allPlans == null) {
            Toast.makeText(activity, "Нет данных для экспорта", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!exportSubjects && !exportLessons && !exportTeachers && !exportAttestation) {
            Toast.makeText(activity, "Выберите хотя бы одну опцию для экспорта", Toast.LENGTH_SHORT).show();
            return;
        }

        new ExportToPdfTask(activity, allPlans, activity.semesterSpinner.getText().toString(),
                exportSubjects, exportLessons, exportTeachers, exportAttestation).execute();
    }

    public static void exportToExcel(ExportActivity activity, List<PerformanceResponse.Plan> allPlans, String selectedSemester,
                                     boolean exportSubjects, boolean exportLessons, boolean exportTeachers, boolean exportAttestation, Bitmap chartBitmap) {
        if (allPlans == null) {
            Toast.makeText(activity, "Нет данных для экспорта", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!exportSubjects && !exportLessons && !exportTeachers && !exportAttestation) {
            Toast.makeText(activity, "Выберите хотя бы одну опцию для экспорта", Toast.LENGTH_SHORT).show();
            return;
        }

        new ExportToExcelTask(activity, allPlans, selectedSemester, exportSubjects, exportLessons, exportTeachers, exportAttestation, null).execute();
    }

    public static float calculateAverageMark(List<PerformanceResponse.Plan> allPlans, String selectedSemester) {
        List<Float> marks = new ArrayList<>();
        for (PerformanceResponse.Plan plan : allPlans) {
            for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
                if (selectedSemester.isEmpty() || period.getName().equals(selectedSemester)) {
                    for (PerformanceResponse.Plan.Period.PlanCell cell : period.getPlanCells()) {
                        for (PerformanceResponse.Plan.Period.PlanCell.Sheet sheet : cell.getSheets()) {
                            for (PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson : sheet.getLessons()) {
                                String mark = lesson.getMarkName();
                                float score;
                                if (mark != null) {
                                    if (mark.equalsIgnoreCase("зачет")) {
                                        score = 5.0f;
                                    } else if (mark.equalsIgnoreCase("незачет")) {
                                        score = 2.0f;
                                    } else {
                                        try {
                                            score = Float.parseFloat(mark);
                                        } catch (NumberFormatException e) {
                                            continue;
                                        }
                                    }
                                    if (score > 0) {
                                        marks.add(score);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        float sum = 0;
        for (Float mark : marks) {
            sum += mark;
        }
        return marks.isEmpty() ? 0 : sum / marks.size();
    }

    public static void shareFile(ExportActivity activity, File file, String mimeType) {
        Uri fileUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(Intent.createChooser(intent, "Поделиться отчётом"));
    }
}