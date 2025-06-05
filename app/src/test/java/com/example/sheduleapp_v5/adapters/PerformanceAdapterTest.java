package com.example.sheduleapp_v5.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.sheduleapp_v5.R;
import com.example.sheduleapp_v5.models.PerformanceResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class PerformanceAdapterTest {

    private Context context;
    private PerformanceAdapter adapter;
    private List<PerformanceResponse.Plan.Period.PlanCell> planCells;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        planCells = new ArrayList<>();
        adapter = new PerformanceAdapter(context, planCells);
    }

    @Test
    public void testGetTeacherNames_emptySheets() {
        List<PerformanceResponse.Plan.Period.PlanCell.Sheet> sheets = Collections.emptyList();
        String result = adapter.getTeacherNames(sheets);
        assertEquals("", result);
    }

    @Test
    public void testGetTeacherNames_singleTeacher() {
        PerformanceResponse.Plan.Period.PlanCell.Sheet sheet = new PerformanceResponse.Plan.Period.PlanCell.Sheet();
        sheet.setTeacherName("Иванов И.И.");
        List<PerformanceResponse.Plan.Period.PlanCell.Sheet> sheets = Collections.singletonList(sheet);

        String result = adapter.getTeacherNames(sheets);
        assertEquals("Иванов И.И.", result);
    }

    @Test
    public void testGetTeacherNames_multipleTeachers() {
        PerformanceResponse.Plan.Period.PlanCell.Sheet sheet1 = new PerformanceResponse.Plan.Period.PlanCell.Sheet();
        sheet1.setTeacherName("Иванов И.И.");
        PerformanceResponse.Plan.Period.PlanCell.Sheet sheet2 = new PerformanceResponse.Plan.Period.PlanCell.Sheet();
        sheet2.setTeacherName("Петров П.П.");
        List<PerformanceResponse.Plan.Period.PlanCell.Sheet> sheets = Arrays.asList(sheet1, sheet2);

        String result = adapter.getTeacherNames(sheets);
        assertEquals("Иванов И.И., Петров П.П.", result);
    }

    @Test
    public void testCalculatePerformancePercentage_noLessons() {
        List<PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson> lessons = Collections.emptyList();
        double result = adapter.calculatePerformancePercentage(lessons);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void testCalculatePerformancePercentage_withLessons() {
        PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson1 = new PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson();
        lesson1.setMarkName("5");
        PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson2 = new PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson();
        lesson2.setMarkName("Н");
        PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson3 = new PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson();
        lesson3.setMarkName("НУ");
        List<PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson> lessons = Arrays.asList(lesson1, lesson2, lesson3);

        double result = adapter.calculatePerformancePercentage(lessons);
        assertEquals(50.0, result, 0.01);
    }

    @Test
    public void testFormatDate_invalidDate() {
        String inputDate = "invalid_date";
        String result = adapter.formatDate(inputDate);
        assertEquals("invalid_date", result);
    }

    @Test
    public void testFormatDate_validDate() {
        String inputDate = "25.12.2023 14:30:00.000";
        String result = adapter.formatDate(inputDate);
        assertEquals("25.12.2023", result);
    }

    @Test
    public void testOnBindViewHolder() {
        View view = LayoutInflater.from(context).inflate(R.layout.performance_item, null, false);
        PerformanceAdapter.PerformanceViewHolder viewHolder = new PerformanceAdapter.PerformanceViewHolder(view);

        PerformanceResponse.Plan.Period.PlanCell planCell = new PerformanceResponse.Plan.Period.PlanCell();
        planCell.setRowName("Математика");

        PerformanceResponse.Plan.Period.PlanCell.Sheet sheet = new PerformanceResponse.Plan.Period.PlanCell.Sheet();
        sheet.setTeacherName("Иванов И.И.");
        sheet.setCurrentAttestationMarkName("Зачет"); // Чтобы isAttested = true
        PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson = new PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson();
        lesson.setMarkName("5");
        sheet.setLessons(Collections.singletonList(lesson));
        planCell.setSheets(Collections.singletonList(sheet));

        PerformanceResponse.Plan.Period.PlanCell.Attestation attestation = new PerformanceResponse.Plan.Period.PlanCell.Attestation();
        attestation.setMarkName("4");
        planCell.setAttestation(attestation);

        planCells.add(planCell);

        adapter.onBindViewHolder(viewHolder, 0);

        assertEquals("Математика", viewHolder.subjectName.getText().toString());
        assertEquals("Иванов И.И.", viewHolder.subjectCodeTextView.getText().toString());
        assertEquals("100.00%", viewHolder.percentageTextView.getText().toString());
        assertEquals("Итог: 4", viewHolder.attendanceText.getText().toString());
        assertEquals("Аттестован ✔️", viewHolder.attestationStatus.getText().toString());
    }

    @Test
    public void testGetItemCount() {
        planCells.add(new PerformanceResponse.Plan.Period.PlanCell());
        planCells.add(new PerformanceResponse.Plan.Period.PlanCell());
        assertEquals(2, adapter.getItemCount());
    }
}