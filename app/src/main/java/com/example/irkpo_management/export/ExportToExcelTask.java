package com.example.irkpo_management.export;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.irkpo_management.ExportActivity;
import com.example.irkpo_management.models.PerformanceResponse;
import com.example.irkpo_management.utils.ExportUtils;
import com.example.irkpo_management.utils.TeacherUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExportToExcelTask extends AsyncTask<Void, Map<String, Integer>, File> {
    private ExportActivity activity;
    private final List<PerformanceResponse.Plan> allPlans;
    private final String selectedSemester;
    private final boolean exportSubjects;
    private final boolean exportLessons;
    private final boolean exportTeachers;
    private final boolean exportAttestation;
    private Bitmap chartBitmap;

    public ExportToExcelTask(ExportActivity activity, List<PerformanceResponse.Plan> allPlans, String selectedSemester,
                             boolean exportSubjects, boolean exportLessons, boolean exportTeachers, boolean exportAttestation, Bitmap chartBitmap) {
        this.activity = activity;
        this.allPlans = allPlans;
        this.selectedSemester = selectedSemester;
        this.exportSubjects = exportSubjects;
        this.exportLessons = exportLessons;
        this.exportTeachers = exportTeachers;
        this.exportAttestation = exportAttestation;
        this.chartBitmap = chartBitmap;
    }

    @Override
    protected void onPreExecute() {
        activity.showProgressBar();
    }

    @Override
    protected File doInBackground(Void... voids) {
        File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "performance_report.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // Создание стилей
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setColor(IndexedColors.DARK_GREEN.getIndex());
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd.MM.yyyy"));

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle absenceStyle = workbook.createCellStyle();
            absenceStyle.cloneStyleFrom(dataStyle);
            absenceStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            absenceStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle excusedAbsenceStyle = workbook.createCellStyle();
            excusedAbsenceStyle.cloneStyleFrom(dataStyle);
            excusedAbsenceStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            excusedAbsenceStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle statsLabelStyle = workbook.createCellStyle();
            Font statsLabelFont = workbook.createFont();
            statsLabelFont.setBold(true);
            statsLabelStyle.setFont(statsLabelFont);

            // Лист 1: Статистика оценок и пропусков
            XSSFSheet statsSheet = workbook.createSheet("Статистика");
            // Добавляем заголовок отчёта
            Row titleRow = statsSheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Отчёт по успеваемости (" + (selectedSemester.isEmpty() ? "Все семестры" : selectedSemester) + ")");
            titleCell.setCellStyle(titleStyle);
            statsSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            Row dateRow = statsSheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Дата создания: " + new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
            statsSheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

            // Таблица статистики
            Row statsHeader = statsSheet.createRow(3);
            statsHeader.createCell(0).setCellValue("Оценка");
            statsHeader.createCell(1).setCellValue("Количество");
            statsHeader.getCell(0).setCellStyle(headerStyle);
            statsHeader.getCell(1).setCellStyle(headerStyle);
            Map<String, Integer> gradeCounts = countGradesAndAbsences();
            int statsRowIdx = 4;
            Row row5 = statsSheet.createRow(statsRowIdx);
            row5.createCell(0).setCellValue("5");
            row5.createCell(1).setCellValue(gradeCounts.getOrDefault("5", 0));
            row5.getCell(0).setCellStyle(dataStyle);
            row5.getCell(1).setCellStyle(dataStyle);
            statsRowIdx++;
            Row row4 = statsSheet.createRow(statsRowIdx);
            row4.createCell(0).setCellValue("4");
            row4.createCell(1).setCellValue(gradeCounts.getOrDefault("4", 0));
            row4.getCell(0).setCellStyle(dataStyle);
            row4.getCell(1).setCellStyle(dataStyle);
            statsRowIdx++;
            Row row3 = statsSheet.createRow(statsRowIdx);
            row3.createCell(0).setCellValue("3");
            row3.createCell(1).setCellValue(gradeCounts.getOrDefault("3", 0));
            row3.getCell(0).setCellStyle(dataStyle);
            row3.getCell(1).setCellStyle(dataStyle);
            statsRowIdx++;
            Row row2 = statsSheet.createRow(statsRowIdx);
            row2.createCell(0).setCellValue("2");
            row2.createCell(1).setCellValue(gradeCounts.getOrDefault("2", 0));
            row2.getCell(0).setCellStyle(dataStyle);
            row2.getCell(1).setCellStyle(dataStyle);
            statsRowIdx++;
            Row rowN = statsSheet.createRow(statsRowIdx);
            rowN.createCell(0).setCellValue("Н (неуважительный прогул)");
            rowN.createCell(1).setCellValue(gradeCounts.getOrDefault("Н", 0));
            rowN.getCell(0).setCellStyle(absenceStyle);
            rowN.getCell(1).setCellStyle(absenceStyle);
            statsRowIdx++;
            Row rowNU = statsSheet.createRow(statsRowIdx);
            rowNU.createCell(0).setCellValue("НУ (уважительный прогул)");
            rowNU.createCell(1).setCellValue(gradeCounts.getOrDefault("НУ", 0));
            rowNU.getCell(0).setCellStyle(excusedAbsenceStyle);
            rowNU.getCell(1).setCellStyle(excusedAbsenceStyle);

            // Дополнительная статистика
            statsRowIdx += 2;
            float averageMark = ExportUtils.calculateAverageMark(allPlans, selectedSemester);
            Row avgMarkRow = statsSheet.createRow(statsRowIdx);
            avgMarkRow.createCell(0).setCellValue("Средний балл:");
            avgMarkRow.createCell(1).setCellValue(String.format("%.2f", averageMark));
            avgMarkRow.getCell(0).setCellStyle(statsLabelStyle);
            avgMarkRow.getCell(1).setCellStyle(dataStyle);

            statsRowIdx++;
            int totalLessons = calculateTotalLessons();
            Row totalLessonsRow = statsSheet.createRow(statsRowIdx);
            totalLessonsRow.createCell(0).setCellValue("Общее количество уроков:");
            totalLessonsRow.createCell(1).setCellValue(totalLessons);
            totalLessonsRow.getCell(0).setCellStyle(statsLabelStyle);
            totalLessonsRow.getCell(1).setCellStyle(dataStyle);

            statsRowIdx++;
            int unexcusedAbsences = gradeCounts.getOrDefault("Н", 0);
            int excusedAbsences = gradeCounts.getOrDefault("НУ", 0);
            int totalAbsences = unexcusedAbsences + excusedAbsences;
            double absencePercentage = totalLessons > 0 ? (totalAbsences * 100.0 / totalLessons) : 0;
            Row absenceRateRow = statsSheet.createRow(statsRowIdx);
            absenceRateRow.createCell(0).setCellValue("Процент пропусков:");
            absenceRateRow.createCell(1).setCellValue(String.format("%.2f%%", absencePercentage));
            absenceRateRow.getCell(0).setCellStyle(statsLabelStyle);
            absenceRateRow.getCell(1).setCellStyle(dataStyle);

            // Передаём данные для создания графика в UI-поток
            publishProgress(gradeCounts);

            // Ждём, пока chartBitmap не будет создан в onProgressUpdate
            while (chartBitmap == null) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Log.e("ExportToExcelTask", "Interrupted while waiting for chart bitmap", e);
                }
            }

            // Вставляем график
            if (chartBitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                chartBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] chartBytes = baos.toByteArray();

                XSSFDrawing drawing = statsSheet.createDrawingPatriarch();
                // Создаем якорь с нужными параметрами
                XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 0, statsRowIdx + 2, 6, statsRowIdx + 20);

                // Устанавливаем тип якоря, чтобы изображение изменяло размер вместе с ячейками
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);

                // Создаем изображение
                drawing.createPicture(anchor, workbook.addPicture(chartBytes, Workbook.PICTURE_TYPE_PNG));
            } else {
                Log.e("ExportToExcelTask", "Chart bitmap is null after waiting");
            }

            // Лист 2: Дисциплины
            if (exportSubjects) {
                XSSFSheet subjectsSheet = workbook.createSheet("Дисциплины");
                Row subjectsHeader = subjectsSheet.createRow(0);
                subjectsHeader.createCell(0).setCellValue("Дисциплина");
                subjectsHeader.getCell(0).setCellStyle(headerStyle);
                Set<String> subjects = new HashSet<>();
                for (PerformanceResponse.Plan plan : allPlans) {
                    for (PerformanceResponse.Plan.Period period : plan.periods) {
                        if (selectedSemester.isEmpty() || period.name.equals(selectedSemester)) {
                            for (PerformanceResponse.Plan.Period.PlanCell cell : period.planCells) {
                                String subject = cell.rowName != null ? cell.rowName : "-";
                                subjects.add(subject);
                            }
                        }
                    }
                }
                int subjectsRowIdx = 1;
                for (String subject : subjects) {
                    Row row = subjectsSheet.createRow(subjectsRowIdx++);
                    row.createCell(0).setCellValue(subject);
                    row.getCell(0).setCellStyle(dataStyle);
                }
                subjectsSheet.setColumnWidth(0, 40 * 256);
            }

            // Лист 3: Уроки (полностью новый формат - сводная таблица)
            if (exportLessons) {
                XSSFSheet lessonsSheet = workbook.createSheet("Уроки");

                // Сначала соберем все уникальные даты уроков
                Set<String> allDates = new HashSet<>();
                Map<String, Map<String, String>> subjectDateMarks = new HashMap<>();

                for (PerformanceResponse.Plan plan : allPlans) {
                    for (PerformanceResponse.Plan.Period period : plan.periods) {
                        if (selectedSemester.isEmpty() || period.name.equals(selectedSemester)) {
                            for (PerformanceResponse.Plan.Period.PlanCell cell : period.planCells) {
                                String subject = cell.rowName != null ? cell.rowName : "-";

                                for (PerformanceResponse.Plan.Period.PlanCell.Sheet sheet : cell.sheets) {
                                    String teacher = sheet.teacherName != null ? sheet.teacherName : "-";
                                    String subjectKey = subject + " (" + teacher + ")";

                                    if (!subjectDateMarks.containsKey(subjectKey)) {
                                        subjectDateMarks.put(subjectKey, new HashMap<>());
                                    }

                                    for (PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson : sheet.lessons) {
                                        String formattedDate = "";
                                        try {
                                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
                                            // Убираем время из форматированной даты
                                            formattedDate = outputFormat.format(inputFormat.parse(
                                                    lesson.lessonDate != null ? lesson.lessonDate : "1970-01-01 00:00:00.000"));
                                        } catch (Exception e) {
                                            // В случае ошибки, используем только часть строки до пробела (если есть)
                                            String rawDate = lesson.lessonDate != null ? lesson.lessonDate : "-";
                                            formattedDate = rawDate.contains(" ") ? rawDate.split(" ")[0] : rawDate;
                                        }

                                        allDates.add(formattedDate);
                                        subjectDateMarks.get(subjectKey).put(formattedDate,
                                                lesson.markName != null ? lesson.markName : "-");
                                    }
                                }
                            }
                        }
                    }
                }

                // Преобразуем даты в список и сортируем
                List<String> sortedDates = new ArrayList<>(allDates);
                Collections.sort(sortedDates, (d1, d2) -> {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                        return format.parse(d1).compareTo(format.parse(d2));
                    } catch (Exception e) {
                        return d1.compareTo(d2);
                    }
                });

                // Создаем заголовок таблицы
                Row headerRow = lessonsSheet.createRow(0);
                headerRow.createCell(0).setCellValue("Дисциплина (Преподаватель)");

                for (int i = 0; i < sortedDates.size(); i++) {
                    Cell dateHeaderCell = headerRow.createCell(i + 1);
                    // Используем только дату без времени
                    String dateOnly = sortedDates.get(i).split(" ")[0];
                    dateHeaderCell.setCellValue(dateOnly);
                    dateHeaderCell.setCellStyle(headerStyle);
                }
                headerRow.getCell(0).setCellStyle(headerStyle);

                // Заполняем таблицу данными
                List<String> sortedSubjects = new ArrayList<>(subjectDateMarks.keySet());
                Collections.sort(sortedSubjects);

                for (int rowIdx = 0; rowIdx < sortedSubjects.size(); rowIdx++) {
                    String subjectKey = sortedSubjects.get(rowIdx);
                    Row row = lessonsSheet.createRow(rowIdx + 1);

                    Cell subjectCell = row.createCell(0);
                    subjectCell.setCellValue(subjectKey);
                    subjectCell.setCellStyle(dataStyle);

                    Map<String, String> dateMarks = subjectDateMarks.get(subjectKey);

                    for (int colIdx = 0; colIdx < sortedDates.size(); colIdx++) {
                        String date = sortedDates.get(colIdx);
                        String mark = dateMarks.getOrDefault(date, "");

                        if (!mark.isEmpty()) {
                            Cell markCell = row.createCell(colIdx + 1);
                            markCell.setCellValue(mark);

                            // Устанавливаем стиль ячейки в зависимости от оценки
                            CellStyle cellStyle = "Н".equalsIgnoreCase(mark) ? absenceStyle :
                                    "НУ".equalsIgnoreCase(mark) ? excusedAbsenceStyle : dataStyle;
                            markCell.setCellStyle(cellStyle);
                        }
                    }
                }

                // Настраиваем ширину столбцов
                lessonsSheet.setColumnWidth(0, 50 * 256); // Дисциплина с преподавателем
                for (int i = 1; i <= sortedDates.size(); i++) {
                    lessonsSheet.setColumnWidth(i, 12 * 256); // Даты
                }

                // Настраиваем автофильтр для удобства работы с таблицей
                lessonsSheet.setAutoFilter(new CellRangeAddress(0, sortedSubjects.size(), 0, sortedDates.size()));

                // Замораживаем первый столбец и строку для удобной навигации
                lessonsSheet.createFreezePane(1, 1);
            }

            // Лист 4: Итоговая аттестация
            if (exportAttestation) {
                XSSFSheet attestationSheet = workbook.createSheet("Итоговая аттестация");
                Row attestationHeader = attestationSheet.createRow(0);
                attestationHeader.createCell(0).setCellValue("Дисциплина");
                attestationHeader.createCell(1).setCellValue("Семестр");
                attestationHeader.createCell(2).setCellValue("Итог");
                for (int i = 0; i < 3; i++) attestationHeader.getCell(i).setCellStyle(headerStyle);
                int attestationRowIdx = 1;
                for (PerformanceResponse.Plan plan : allPlans) {
                    for (PerformanceResponse.Plan.Period period : plan.periods) {
                        if (selectedSemester.isEmpty() || period.name.equals(selectedSemester)) {
                            for (PerformanceResponse.Plan.Period.PlanCell cell : period.planCells) {
                                Row row = attestationSheet.createRow(attestationRowIdx++);
                                row.createCell(0).setCellValue(cell.rowName != null ? cell.rowName : "-");
                                row.createCell(1).setCellValue(period.name);
                                Cell markCell = row.createCell(2);
                                String mark = cell.attestation != null ? cell.attestation.markName : "-";
                                markCell.setCellValue(mark);
                                // Применяем стиль в зависимости от оценки
                                if ("Н".equalsIgnoreCase(mark)) {
                                    for (int i = 0; i < 3; i++) row.getCell(i).setCellStyle(absenceStyle);
                                } else if ("НУ".equalsIgnoreCase(mark)) {
                                    for (int i = 0; i < 3; i++) row.getCell(i).setCellStyle(excusedAbsenceStyle);
                                } else {
                                    for (int i = 0; i < 3; i++) row.getCell(i).setCellStyle(dataStyle);
                                }
                            }
                        }
                    }
                }
                attestationSheet.setColumnWidth(0, 40 * 256);
                attestationSheet.setColumnWidth(1, 15 * 256);
                attestationSheet.setColumnWidth(2, 10 * 256);
            }

            // Лист 5: Преподаватели
            if (exportTeachers) {
                XSSFSheet teachersSheet = workbook.createSheet("Преподаватели");
                Row teachersHeader = teachersSheet.createRow(0);
                teachersHeader.createCell(0).setCellValue("Имя");
                teachersHeader.createCell(1).setCellValue("ID");
                for (int i = 0; i < 2; i++) teachersHeader.getCell(i).setCellStyle(headerStyle);
                int teachersRowIdx = 1;
                for (Map.Entry<String, String> teacher : TeacherUtils.getAllTeachers().entrySet()) {
                    Row row = teachersSheet.createRow(teachersRowIdx++);
                    row.createCell(0).setCellValue(teacher.getKey());
                    row.createCell(1).setCellValue(teacher.getValue());
                    row.getCell(0).setCellStyle(dataStyle);
                    row.getCell(1).setCellStyle(dataStyle);
                }
                teachersSheet.setColumnWidth(0, 30 * 256);
                teachersSheet.setColumnWidth(1, 20 * 256);
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
            for (PerformanceResponse.Plan.Period period : plan.periods) {
                if (selectedSemester.isEmpty() || period.name.equals(selectedSemester)) {
                    for (PerformanceResponse.Plan.Period.PlanCell cell : period.planCells) {
                        for (PerformanceResponse.Plan.Period.PlanCell.Sheet sheet : cell.sheets) {
                            for (PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson : sheet.lessons) {
                                String mark = lesson.markName;
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

    private int calculateTotalLessons() {
        int total = 0;
        for (PerformanceResponse.Plan plan : allPlans) {
            for (PerformanceResponse.Plan.Period period : plan.periods) {
                if (selectedSemester.isEmpty() || period.name.equals(selectedSemester)) {
                    for (PerformanceResponse.Plan.Period.PlanCell cell : period.planCells) {
                        for (PerformanceResponse.Plan.Period.PlanCell.Sheet sheet : cell.sheets) {
                            total += sheet.lessons.size();
                        }
                    }
                }
            }
        }
        return total;
    }

    @Override
    protected void onProgressUpdate(Map<String, Integer>... values) {
        Map<String, Integer> gradeCounts = values[0];
        try {
            BarChart chart = new BarChart(activity);
            chart.setDrawingCacheEnabled(true);
            chart.setBackgroundColor(android.graphics.Color.WHITE);

// Улучшенная подготовка данных
            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0, gradeCounts.getOrDefault("5", 0)));
            entries.add(new BarEntry(1, gradeCounts.getOrDefault("4", 0)));
            entries.add(new BarEntry(2, gradeCounts.getOrDefault("3", 0)));
            entries.add(new BarEntry(3, gradeCounts.getOrDefault("2", 0)));
            entries.add(new BarEntry(4, gradeCounts.getOrDefault("Н", 0)));
            entries.add(new BarEntry(5, gradeCounts.getOrDefault("НУ", 0)));

            BarDataSet dataSet = new BarDataSet(entries, "Статистика оценок");
            dataSet.setValueTextSize(14f);
            dataSet.setValueTextColor(android.graphics.Color.BLACK);
            dataSet.setDrawValues(true);

            // Устанавливаем индивидуальные цвета для каждого столбца
            int[] customColors = {
                    android.graphics.Color.rgb(76, 175, 80),    // Зеленый для 5
                    android.graphics.Color.rgb(139, 195, 74),   // Светло-зеленый для 4
                    android.graphics.Color.rgb(255, 193, 7),    // Желтый для 3
                    android.graphics.Color.rgb(244, 67, 54),    // Красный для 2
                    android.graphics.Color.rgb(156, 39, 176),   // Фиолетовый для Н
                    android.graphics.Color.rgb(33, 150, 243)    // Синий для НУ
            };
            dataSet.setColors(customColors);

            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.7f);
            chart.setData(barData);

            // Улучшение осей
            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setTextSize(14f);
            xAxis.setTextColor(android.graphics.Color.BLACK);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"5", "4", "3", "2", "Н", "НУ"}));
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(6, false);

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setTextSize(14f);
            leftAxis.setAxisMinimum(0f);
            // Добавляем интервал сверху
            leftAxis.setSpaceTop(30f);
            leftAxis.setDrawGridLines(true);
            leftAxis.setGranularity(1f);

            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setEnabled(false);

            // Убираем заголовок
            chart.getDescription().setEnabled(false);

            // Увеличиваем размер легенды
            chart.getLegend().setTextSize(14f);
            chart.getLegend().setFormSize(12f);

            // Отключаем сжатие по оси Y
            chart.setDoubleTapToZoomEnabled(false);
            chart.setPinchZoom(false);
            chart.setScaleEnabled(false);

            // Улучшаем размеры и рендеринг
            chart.measure(
                    View.MeasureSpec.makeMeasureSpec(1024, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(768, View.MeasureSpec.EXACTLY)
            );
            chart.layout(0, 0, 1024, 768);
            chart.invalidate();

            // Получаем Bitmap с большей задержкой для завершения рендеринга
            try {
                Thread.sleep(100); // Небольшая задержка для рендеринга
            } catch (InterruptedException e) {
                Log.e("ExportToExcelTask", "Sleep interrupted", e);
            }

            chartBitmap = Bitmap.createBitmap(chart.getDrawingCache());
        } catch (Exception e) {
            Log.e("ExportToExcelTask", "Error generating chart bitmap in UI thread", e);
            chartBitmap = null;
        }
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