package com.example.sheduleapp_v5.export;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.sheduleapp_v5.ExportActivity;
import com.example.sheduleapp_v5.models.PerformanceResponse;
import com.example.sheduleapp_v5.utils.ExportUtils;
import com.example.sheduleapp_v5.utils.TeacherUtils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExportToExcelTask extends AsyncTask<Void, Void, File> {
    private ExportActivity activity;
    private List<PerformanceResponse.Plan> allPlans;
    private String selectedSemester;
    private boolean exportSubjects;
    private boolean exportLessons;
    private boolean exportTeachers;
    private boolean exportAttestation;

    public ExportToExcelTask(ExportActivity activity, List<PerformanceResponse.Plan> allPlans, String selectedSemester,
                             boolean exportSubjects, boolean exportLessons, boolean exportTeachers, boolean exportAttestation) {
        this.activity = activity;
        this.allPlans = allPlans;
        this.selectedSemester = selectedSemester;
        this.exportSubjects = exportSubjects;
        this.exportLessons = exportLessons;
        this.exportTeachers = exportTeachers;
        this.exportAttestation = exportAttestation;
    }

    @Override
    protected void onPreExecute() {
        activity.showProgressBar();
    }

    @Override
    protected File doInBackground(Void... voids) {
        File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "performance_report.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // Лист 1: Статистика оценок и пропусков
            Sheet statsSheet = workbook.createSheet("Статистика");
            Row statsHeader = statsSheet.createRow(0);
            statsHeader.createCell(0).setCellValue("Оценка");
            statsHeader.createCell(1).setCellValue("Количество");
            Map<String, Integer> gradeCounts = countGradesAndAbsences();
            int statsRowIdx = 1;
            statsSheet.createRow(statsRowIdx++).createCell(1).setCellValue(gradeCounts.getOrDefault("5", 0));
            statsSheet.getRow(statsRowIdx - 1).createCell(0).setCellValue("5");
            statsSheet.createRow(statsRowIdx++).createCell(1).setCellValue(gradeCounts.getOrDefault("4", 0));
            statsSheet.getRow(statsRowIdx - 1).createCell(0).setCellValue("4");
            statsSheet.createRow(statsRowIdx++).createCell(1).setCellValue(gradeCounts.getOrDefault("3", 0));
            statsSheet.getRow(statsRowIdx - 1).createCell(0).setCellValue("3");
            statsSheet.createRow(statsRowIdx++).createCell(1).setCellValue(gradeCounts.getOrDefault("2", 0));
            statsSheet.getRow(statsRowIdx - 1).createCell(0).setCellValue("2");
            statsSheet.createRow(statsRowIdx++).createCell(1).setCellValue(gradeCounts.getOrDefault("Н", 0));
            statsSheet.getRow(statsRowIdx - 1).createCell(0).setCellValue("Н (неуважительный прогул)");
            statsSheet.createRow(statsRowIdx++).createCell(1).setCellValue(gradeCounts.getOrDefault("НУ", 0));
            statsSheet.getRow(statsRowIdx - 1).createCell(0).setCellValue("НУ (уважительный прогул)");

            // Лист 2: Дисциплины
            if (exportSubjects) {
                Sheet subjectsSheet = workbook.createSheet("Дисциплины");
                Row subjectsHeader = subjectsSheet.createRow(0);
                subjectsHeader.createCell(0).setCellValue("Дисциплина");
                Set<String> subjects = new HashSet<>();
                for (PerformanceResponse.Plan plan : allPlans) {
                    for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
                        if (selectedSemester.isEmpty() || period.getName().equals(selectedSemester)) {
                            for (PerformanceResponse.Plan.Period.PlanCell cell : period.getPlanCells()) {
                                String subject = cell.getRowName() != null ? cell.getRowName() : "-";
                                subjects.add(subject);
                            }
                        }
                    }
                }
                int subjectsRowIdx = 1;
                for (String subject : subjects) {
                    subjectsSheet.createRow(subjectsRowIdx++).createCell(0).setCellValue(subject);
                }
            }

            // Лист 3: Уроки
            if (exportLessons) {
                Sheet lessonsSheet = workbook.createSheet("Уроки");
                Row lessonsHeader = lessonsSheet.createRow(0);
                lessonsHeader.createCell(0).setCellValue("Дисциплина");
                lessonsHeader.createCell(1).setCellValue("Оценка");
                lessonsHeader.createCell(2).setCellValue("Дата");
                lessonsHeader.createCell(3).setCellValue("Семестр");
                lessonsHeader.createCell(4).setCellValue("Преподаватель");
                int lessonsRowIdx = 1;
                for (PerformanceResponse.Plan plan : allPlans) {
                    for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
                        if (selectedSemester.isEmpty() || period.getName().equals(selectedSemester)) {
                            for (PerformanceResponse.Plan.Period.PlanCell cell : period.getPlanCells()) {
                                String subject = cell.getRowName() != null ? cell.getRowName() : "-";
                                for (PerformanceResponse.Plan.Period.PlanCell.Sheet sheet : cell.getSheets()) {
                                    for (PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson : sheet.getLessons()) {
                                        Row row = lessonsSheet.createRow(lessonsRowIdx++);
                                        row.createCell(0).setCellValue(subject);
                                        row.createCell(1).setCellValue(lesson.getMarkName() != null ? lesson.getMarkName() : "-");
                                        row.createCell(2).setCellValue(lesson.getLessonDate() != null ? lesson.getLessonDate() : "-");
                                        row.createCell(3).setCellValue(period.getName());
                                        row.createCell(4).setCellValue(sheet.getTeacherName() != null ? sheet.getTeacherName() : "-");
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Лист 4: Итоговая аттестация
            if (exportAttestation) {
                Sheet attestationSheet = workbook.createSheet("Итоговая аттестация");
                Row attestationHeader = attestationSheet.createRow(0);
                attestationHeader.createCell(0).setCellValue("Дисциплина");
                attestationHeader.createCell(1).setCellValue("Семестр");
                attestationHeader.createCell(2).setCellValue("Итог");
                int attestationRowIdx = 1;
                for (PerformanceResponse.Plan plan : allPlans) {
                    for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
                        if (selectedSemester.isEmpty() || period.getName().equals(selectedSemester)) {
                            for (PerformanceResponse.Plan.Period.PlanCell cell : period.getPlanCells()) {
                                Row row = attestationSheet.createRow(attestationRowIdx++);
                                row.createCell(0).setCellValue(cell.getRowName() != null ? cell.getRowName() : "-");
                                row.createCell(1).setCellValue(period.getName());
                                row.createCell(2).setCellValue(cell.getAttestation() != null ? cell.getAttestation().getMarkName() : "-");
                            }
                        }
                    }
                }
            }

            // Лист 5: Преподаватели
            if (exportTeachers) {
                Sheet teachersSheet = workbook.createSheet("Преподаватели");
                Row teachersHeader = teachersSheet.createRow(0);
                teachersHeader.createCell(0).setCellValue("Имя");
                teachersHeader.createCell(1).setCellValue("ID");
                int teachersRowIdx = 1;
                for (Map.Entry<String, String> teacher : TeacherUtils.getAllTeachers().entrySet()) {
                    Row row = teachersSheet.createRow(teachersRowIdx++);
                    row.createCell(0).setCellValue(teacher.getKey());
                    row.createCell(1).setCellValue(teacher.getValue());
                }
            }

            // Сохранение файла
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            return file;
        } catch (Exception e) {
            Log.e("ExportActivity", "Excel export error", e);
            return null;
        }
    }

    private Map<String, Integer> countGradesAndAbsences() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("5", 0);
        counts.put("4", 0);
        counts.put("3", 0);
        counts.put("2", 0);
        counts.put("Н", 0);
        counts.put("НУ", 0);

        for (PerformanceResponse.Plan plan : allPlans) {
            for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
                if (selectedSemester.isEmpty() || period.getName().equals(selectedSemester)) {
                    for (PerformanceResponse.Plan.Period.PlanCell cell : period.getPlanCells()) {
                        for (PerformanceResponse.Plan.Period.PlanCell.Sheet sheet : cell.getSheets()) {
                            for (PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson : sheet.getLessons()) {
                                String mark = lesson.getMarkName();
                                if (mark != null) {
                                    if (mark.matches("[2-5]")) {
                                        counts.put(mark, counts.getOrDefault(mark, 0) + 1);
                                    } else if ("Н".equalsIgnoreCase(mark)) {
                                        counts.put("Н", counts.getOrDefault("Н", 0) + 1);
                                    } else if ("НУ".equalsIgnoreCase(mark)) {
                                        counts.put("НУ", counts.getOrDefault("НУ", 0) + 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return counts;
    }

    @Override
    protected void onPostExecute(File file) {
        activity.hideProgressBar();
        if (file != null) {
            ExportUtils.shareFile(activity, file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        } else {
            Toast.makeText(activity, "Ошибка создания Excel", Toast.LENGTH_LONG).show();
        }
    }
}