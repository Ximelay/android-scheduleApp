package com.example.stud_informer_irkpo.export;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.stud_informer_irkpo.ExportActivity;
import com.example.stud_informer_irkpo.models.PerformanceResponse;
import com.example.stud_informer_irkpo.utils.ExportUtils;
import com.example.stud_informer_irkpo.utils.TeacherUtils;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExportToPdfTask extends AsyncTask<Void, Void, File> {
    private ExportActivity activity;
    private List<PerformanceResponse.Plan> allPlans;
    private boolean exportSubjects;
    private boolean exportLessons;
    private boolean exportTeachers;
    private boolean exportAttestation;
    private String selectedSemester;

    public ExportToPdfTask(ExportActivity activity, List<PerformanceResponse.Plan> allPlans, String selectedSemester,
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
        File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "performance_report.pdf");
        try {
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont font = PdfFontFactory.createFont("assets/fonts/arial.ttf", "CP1251", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            document.setFont(font);

            if (exportAttestation) {
                document.add(new Paragraph("Итоговая аттестация:")
                        .setFont(font)
                        .setFontSize(14)
                        .setBold());
                Table attestationTable = new Table(new float[]{300, 100, 100});
                attestationTable.setWidth(500);

                attestationTable.addHeaderCell(new Cell()
                        .add(new Paragraph("Дисциплина").setFont(font).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER));
                attestationTable.addHeaderCell(new Cell()
                        .add(new Paragraph("Семестр").setFont(font).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER));
                attestationTable.addHeaderCell(new Cell()
                        .add(new Paragraph("Итог").setFont(font).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER));

                Map<String, String> finalMarks = new HashMap<>();
                for (PerformanceResponse.Plan plan : allPlans) {
                    for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
                        if (selectedSemester.isEmpty() || period.getName().equals(selectedSemester)) {
                            for (PerformanceResponse.Plan.Period.PlanCell cell : period.getPlanCells()) {
                                String subject = cell.getRowName() != null ? cell.getRowName() : "-";
                                String mark = cell.getAttestation() != null ? cell.getAttestation().getMarkName() : "-";
                                if (!mark.equals("-")) {
                                    finalMarks.put(subject, mark);
                                }
                            }
                        }
                    }
                }
                for (Map.Entry<String, String> entry : finalMarks.entrySet()) {
                    attestationTable.addCell(new Cell()
                            .add(new Paragraph(entry.getKey()).setFont(font))
                            .setTextAlignment(TextAlignment.LEFT));
                    attestationTable.addCell(new Cell()
                            .add(new Paragraph(selectedSemester.isEmpty() ? "Все" : selectedSemester).setFont(font))
                            .setTextAlignment(TextAlignment.CENTER));
                    attestationTable.addCell(new Cell()
                            .add(new Paragraph(entry.getValue()).setFont(font))
                            .setTextAlignment(TextAlignment.CENTER));
                }
                if (!finalMarks.isEmpty()) {
                    document.add(attestationTable);
                } else {
                    document.add(new Paragraph("Итоговые оценки отсутствуют.")
                            .setFont(font)
                            .setFontSize(12));
                }
                document.add(new Paragraph("\n"));
            }

            if (exportSubjects) {
                document.add(new Paragraph("Названия дисциплин:")
                        .setFont(font)
                        .setFontSize(14)
                        .setBold());
                Table subjectsTable = new Table(new float[]{300});
                subjectsTable.setWidth(500);

                Cell headerCell = new Cell()
                        .add(new Paragraph("Дисциплина").setFont(font).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER);
                subjectsTable.addHeaderCell(headerCell);

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
                for (String subject : subjects) {
                    subjectsTable.addCell(new Cell()
                            .add(new Paragraph(subject).setFont(font))
                            .setTextAlignment(TextAlignment.LEFT));
                }
                document.add(subjectsTable);
            }

            if (exportLessons) {
                document.add(new Paragraph("\nУроки (средние оценки):")
                        .setFont(font)
                        .setFontSize(14)
                        .setBold());
                Table lessonsTable = new Table(new float[]{150, 80, 150});
                lessonsTable.setWidth(500);

                lessonsTable.addHeaderCell(new Cell()
                        .add(new Paragraph("Дисциплина").setFont(font).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER));
                lessonsTable.addHeaderCell(new Cell()
                        .add(new Paragraph("Средняя оценка").setFont(font).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER));
                lessonsTable.addHeaderCell(new Cell()
                        .add(new Paragraph("Преподаватель").setFont(font).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER));

                Map<String, List<Float>> subjectMarks = new HashMap<>();
                Map<String, String> subjectTeachers = new HashMap<>();
                for (PerformanceResponse.Plan plan : allPlans) {
                    for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
                        if (selectedSemester.isEmpty() || period.getName().equals(selectedSemester)) {
                            for (PerformanceResponse.Plan.Period.PlanCell cell : period.getPlanCells()) {
                                String subject = cell.getRowName() != null ? cell.getRowName() : "-";
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
                                            subjectMarks.computeIfAbsent(subject, k -> new ArrayList<>()).add(score);
                                        }
                                        if (sheet.getTeacherName() != null && !subjectTeachers.containsKey(subject)) {
                                            subjectTeachers.put(subject, sheet.getTeacherName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                for (Map.Entry<String, List<Float>> entry : subjectMarks.entrySet()) {
                    String subject = entry.getKey();
                    List<Float> marks = entry.getValue();
                    float average = marks.isEmpty() ? 0 : marks.stream().reduce(0f, Float::sum) / marks.size();
                    lessonsTable.addCell(new Cell()
                            .add(new Paragraph(subject).setFont(font))
                            .setTextAlignment(TextAlignment.LEFT));
                    lessonsTable.addCell(new Cell()
                            .add(new Paragraph(String.format("%.2f", average)).setFont(font))
                            .setTextAlignment(TextAlignment.CENTER));
                    lessonsTable.addCell(new Cell()
                            .add(new Paragraph(subjectTeachers.getOrDefault(subject, "-")).setFont(font))
                            .setTextAlignment(TextAlignment.LEFT));
                }
                document.add(lessonsTable);
            }

            if (exportTeachers) {
                document.add(new Paragraph("\nПреподаватели:")
                        .setFont(font)
                        .setFontSize(14)
                        .setBold());
                Table teachersTable = new Table(new float[]{300, 100});
                teachersTable.setWidth(500);

                teachersTable.addHeaderCell(new Cell()
                        .add(new Paragraph("ФИО").setFont(font).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER));
                teachersTable.addHeaderCell(new Cell()
                        .add(new Paragraph("ID").setFont(font).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER));

                for (Map.Entry<String, String> teacher : TeacherUtils.getAllTeachers().entrySet()) {
                    teachersTable.addCell(new Cell()
                            .add(new Paragraph(teacher.getKey()).setFont(font))
                            .setTextAlignment(TextAlignment.LEFT));
                    teachersTable.addCell(new Cell()
                            .add(new Paragraph(teacher.getValue()).setFont(font))
                            .setTextAlignment(TextAlignment.CENTER));
                }
                document.add(teachersTable);
            }

            document.close();
            return file;
        } catch (Exception e) {
            Log.e("ExportActivity", "PDF export error", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(File file) {
        activity.hideProgressBar();
        if (file != null) {
            ExportUtils.shareFile(activity, file, "application/pdf");
        } else {
            Toast.makeText(activity, "Ошибка создания PDF", Toast.LENGTH_LONG).show();
        }
    }
}