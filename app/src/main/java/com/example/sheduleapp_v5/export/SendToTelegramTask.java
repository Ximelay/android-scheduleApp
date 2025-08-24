//package com.example.sheduleapp_v5.export;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.core.content.FileProvider;
//
//import com.example.sheduleapp_v5.ExportActivity;
//import com.example.sheduleapp_v5.models.PerformanceResponse;
//import com.example.sheduleapp_v5.utils.ExportUtils;
//import com.example.sheduleapp_v5.utils.TeacherUtils;
//import com.itextpdf.kernel.colors.ColorConstants;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Cell;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.element.Table;
//import com.itextpdf.layout.properties.TextAlignment;
//
//import java.io.File;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//public class SendToTelegramTask extends AsyncTask<Void, Void, File> {
//    private ExportActivity activity;
//    private List<PerformanceResponse.Plan> allPlans;
//    private String selectedSemester;
//    private boolean exportSubjects;
//    private boolean exportLessons;
//    private boolean exportTeachers;
//    private boolean exportAttestation;
//
//    public SendToTelegramTask(ExportActivity activity, List<PerformanceResponse.Plan> allPlans, String selectedSemester,
//                              boolean exportSubjects, boolean exportLessons, boolean exportTeachers, boolean exportAttestation) {
//        this.activity = activity;
//        this.allPlans = allPlans;
//        this.selectedSemester = selectedSemester;
//        this.exportSubjects = exportSubjects;
//        this.exportLessons = exportLessons;
//        this.exportTeachers = exportTeachers;
//        this.exportAttestation = exportAttestation;
//    }
//
//    @Override
//    protected void onPreExecute() {
//        activity.showProgressBar();
//    }
//
//    @Override
//    protected File doInBackground(Void... voids) {
//        File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "performance_report.pdf");
//        try {
//            PdfWriter writer = new PdfWriter(file);
//            PdfDocument pdf = new PdfDocument(writer);
//            Document document = new Document(pdf);
//
//            PdfFont font = PdfFontFactory.createFont("assets/fonts/arial.ttf", "CP1251", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
//            document.setFont(font);
//
//            if (exportSubjects) {
//                document.add(new Paragraph("Названия дисциплин:")
//                        .setFont(font)
//                        .setFontSize(14)
//                        .setBold());
//                Table table = new Table(new float[]{300});
//                table.setWidth(500);
//
//                table.addHeaderCell(new Cell()
//                        .add(new Paragraph("Дисциплина").setFont(font).setBold())
//                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                        .setTextAlignment(TextAlignment.CENTER));
//
//                Set<String> subjects = new HashSet<>();
//                for (PerformanceResponse.Plan plan : allPlans) {
//                    for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
//                        if (selectedSemester.isEmpty() || period.getName().equals(selectedSemester)) {
//                            for (PerformanceResponse.Plan.Period.PlanCell cell : period.getPlanCells()) {
//                                String subject = cell.getRowName() != null ? cell.getRowName() : "-";
//                                subjects.add(subject);
//                            }
//                        }
//                    }
//                }
//                for (String subject : subjects) {
//                    table.addCell(new Cell()
//                            .add(new Paragraph(subject).setFont(font))
//                            .setTextAlignment(TextAlignment.LEFT));
//                }
//                document.add(table);
//            }
//
//            if (exportLessons) {
//                document.add(new Paragraph("\nУроки:")
//                        .setFont(font)
//                        .setFontSize(14)
//                        .setBold());
//                Table table = new Table(new float[]{150, 80, 100, 100, 150});
//                table.setWidth(500);
//
//                table.addHeaderCell(new Cell()
//                        .add(new Paragraph("Дисциплина").setFont(font).setBold())
//                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                        .setTextAlignment(TextAlignment.CENTER));
//                table.addHeaderCell(new Cell()
//                        .add(new Paragraph("Оценка").setFont(font).setBold())
//                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                        .setTextAlignment(TextAlignment.CENTER));
//                table.addHeaderCell(new Cell()
//                        .add(new Paragraph("Дата").setFont(font).setBold())
//                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                        .setTextAlignment(TextAlignment.CENTER));
//                table.addHeaderCell(new Cell()
//                        .add(new Paragraph("Семестр").setFont(font).setBold())
//                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                        .setTextAlignment(TextAlignment.CENTER));
//                table.addHeaderCell(new Cell()
//                        .add(new Paragraph("Преподаватель").setFont(font).setBold())
//                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                        .setTextAlignment(TextAlignment.CENTER));
//
//                for (PerformanceResponse.Plan plan : allPlans) {
//                    for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
//                        if (selectedSemester.isEmpty() || period.getName().equals(selectedSemester)) {
//                            for (PerformanceResponse.Plan.Period.PlanCell cell : period.getPlanCells()) {
//                                for (PerformanceResponse.Plan.Period.PlanCell.Sheet sheet : cell.getSheets()) {
//                                    for (PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson : sheet.getLessons()) {
//                                        table.addCell(new Cell()
//                                                .add(new Paragraph(cell.getRowName() != null ? cell.getRowName() : "-").setFont(font))
//                                                .setTextAlignment(TextAlignment.LEFT));
//                                        table.addCell(new Cell()
//                                                .add(new Paragraph(lesson.getMarkName() != null ? lesson.getMarkName() : "-").setFont(font))
//                                                .setTextAlignment(TextAlignment.CENTER));
//                                        table.addCell(new Cell()
//                                                .add(new Paragraph(lesson.getLessonDate() != null ? lesson.getLessonDate() : "-").setFont(font))
//                                                .setTextAlignment(TextAlignment.CENTER));
//                                        table.addCell(new Cell()
//                                                .add(new Paragraph(period.getName()).setFont(font))
//                                                .setTextAlignment(TextAlignment.CENTER));
//                                        table.addCell(new Cell()
//                                                .add(new Paragraph(sheet.getTeacherName() != null ? sheet.getTeacherName() : "-").setFont(font))
//                                                .setTextAlignment(TextAlignment.LEFT));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                document.add(table);
//            }
//
//            if (exportAttestation) {
//                document.add(new Paragraph("\nИтоговая аттестация:")
//                        .setFont(font)
//                        .setFontSize(14)
//                        .setBold());
//                Table table = new Table(new float[]{300, 100, 100});
//                table.setWidth(500);
//
//                table.addHeaderCell(new Cell()
//                        .add(new Paragraph("Дисциплина").setFont(font).setBold())
//                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                        .setTextAlignment(TextAlignment.CENTER));
//                table.addHeaderCell(new Cell()
//                        .add(new Paragraph("Семестр").setFont(font).setBold())
//                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                        .setTextAlignment(TextAlignment.CENTER));
//                table.addHeaderCell(new Cell()
//                        .add(new Paragraph("Итог").setFont(font).setBold())
//                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                        .setTextAlignment(TextAlignment.CENTER));
//
//                for (PerformanceResponse.Plan plan : allPlans) {
//                    for (PerformanceResponse.Plan.Period period : plan.getPeriods()) {
//                        if (selectedSemester.isEmpty() || period.getName().equals(selectedSemester)) {
//                            for (PerformanceResponse.Plan.Period.PlanCell cell : period.getPlanCells()) {
//                                String finalMark = cell.getAttestation() != null ? cell.getAttestation().getMarkName() : null;
//                                table.addCell(new Cell()
//                                        .add(new Paragraph(cell.getRowName() != null ? cell.getRowName() : "-").setFont(font))
//                                        .setTextAlignment(TextAlignment.LEFT));
//                                table.addCell(new Cell()
//                                        .add(new Paragraph(period.getName()).setFont(font))
//                                        .setTextAlignment(TextAlignment.CENTER));
//                                table.addCell(new Cell()
//                                        .add(new Paragraph(finalMark != null ? finalMark : "-").setFont(font))
//                                        .setTextAlignment(TextAlignment.CENTER));
//                            }
//                        }
//                    }
//                }
//                document.add(table);
//            }
//
//            if (exportTeachers) {
//                document.add(new Paragraph("\nПреподаватели:")
//                        .setFont(font)
//                        .setFontSize(14)
//                        .setBold());
//                Table table = new Table(new float[]{300, 100});
//                table.setWidth(500);
//
//                table.addHeaderCell(new Cell()
//                        .add(new Paragraph("ФИО").setFont(font).setBold())
//                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                        .setTextAlignment(TextAlignment.CENTER));
//                table.addHeaderCell(new Cell()
//                        .add(new Paragraph("ID").setFont(font).setBold())
//                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                        .setTextAlignment(TextAlignment.CENTER));
//
//                for (Map.Entry<String, String> teacher : TeacherUtils.getAllTeachers().entrySet()) {
//                    table.addCell(new Cell()
//                            .add(new Paragraph(teacher.getKey()).setFont(font))
//                            .setTextAlignment(TextAlignment.LEFT));
//                    table.addCell(new Cell()
//                            .add(new Paragraph(teacher.getValue()).setFont(font))
//                            .setTextAlignment(TextAlignment.CENTER));
//                }
//                document.add(table);
//            }
//
//            document.close();
//            return file;
//        } catch (Exception e) {
//            Log.e("ExportActivity", "Telegram send error", e);
//            return null;
//        }
//    }
//
//    @Override
//    protected void onPostExecute(File file) {
//        activity.hideProgressBar();
//        if (file != null) {
//            Uri fileUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("application/pdf");
//            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
//            intent.setPackage("org.telegram.messenger");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            StringBuilder caption = new StringBuilder("Отчёт по успеваемости");
//            if (exportLessons) {
//                caption.append("\nСредний балл: ").append(String.format("%.2f", ExportUtils.calculateAverageMark(allPlans, selectedSemester)));
//            }
//            intent.putExtra(Intent.EXTRA_TEXT, caption.toString());
//
//            try {
//                activity.startActivity(Intent.createChooser(intent, "Отправить отчёт"));
//            } catch (Exception e) {
//                Toast.makeText(activity, "Ошибка отправки: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        } else {
//            Toast.makeText(activity, "Ошибка создания PDF для отправки", Toast.LENGTH_LONG).show();
//        }
//    }
//}