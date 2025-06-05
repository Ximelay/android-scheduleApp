package com.example.sheduleapp_v5.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class PerformanceResponseTest {
    @Test
    public void testPerformanceResponseGettersAndSetters() {
        PerformanceResponse response = new PerformanceResponse();
        List<PerformanceResponse.Plan> plans = Arrays.asList(new PerformanceResponse.Plan());
        response.setPlans(plans);

        assertEquals(plans, response.getPlans());
    }

    @Test
    public void testPerformanceResponseWithNullPlans() {
        PerformanceResponse response = new PerformanceResponse();
        response.setPlans(null);

        assertNull(response.getPlans());
    }

    @Test
    public void testPlanGettersAndSetters() {
        PerformanceResponse.Plan plan = new PerformanceResponse.Plan();
        String groupName = "Group A";
        List<PerformanceResponse.Plan.Period> periods = Arrays.asList(new PerformanceResponse.Plan.Period());

        plan.setGroupName(groupName);
        plan.setPeriods(periods);

        assertEquals(groupName, plan.getGroupName());
        assertEquals(periods, plan.getPeriods());
    }

    @Test
    public void testPeriodGettersAndSetters() {
        PerformanceResponse.Plan.Period period = new PerformanceResponse.Plan.Period();
        String name = "Semester 1";
        List<PerformanceResponse.Plan.Period.PlanCell> planCells = Arrays.asList(new PerformanceResponse.Plan.Period.PlanCell());

        period.setName(name);
        period.setPlanCells(planCells);

        assertEquals(name, period.getName());
        assertEquals(planCells, period.getPlanCells());
    }

    @Test
    public void testPlanCellGettersAndSetters() {
        PerformanceResponse.Plan.Period.PlanCell planCell = new PerformanceResponse.Plan.Period.PlanCell();
        String rowIndex = "1";
        String rowName = "Math";
        PerformanceResponse.Plan.Period.PlanCell.Attestation attestation = new PerformanceResponse.Plan.Period.PlanCell.Attestation();
        List<PerformanceResponse.Plan.Period.PlanCell.Sheet> sheets = Arrays.asList(new PerformanceResponse.Plan.Period.PlanCell.Sheet());

        planCell.setRowIndex(rowIndex);
        planCell.setRowName(rowName);
        planCell.setAttestation(attestation);
        planCell.setSheets(sheets);

        assertEquals(rowIndex, planCell.getRowIndex());
        assertEquals(rowName, planCell.getRowName());
        assertEquals(attestation, planCell.getAttestation());
        assertEquals(sheets, planCell.getSheets());
    }

    @Test
    public void testAttestationGettersAndSetters() {
        PerformanceResponse.Plan.Period.PlanCell.Attestation attestation = new PerformanceResponse.Plan.Period.PlanCell.Attestation();
        String name = "Final Exam";
        String markName = "A";

        attestation.setName(name);
        attestation.setMarkName(markName);

        assertEquals(name, attestation.getName());
        assertEquals(markName, attestation.getMarkName());
    }

    @Test
    public void testSheetGettersAndSetters() {
        PerformanceResponse.Plan.Period.PlanCell.Sheet sheet = new PerformanceResponse.Plan.Period.PlanCell.Sheet();
        String teacherName = "Dr. Smith";
        String currentAttestationMarkName = "Pass";
        String sheetAttestationMarkName = "B";
        List<PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson> lessons = Arrays.asList(new PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson());

        sheet.setTeacherName(teacherName);
        sheet.setCurrentAttestationMarkName(currentAttestationMarkName);
        sheet.setSheetAttestationMarkName(sheetAttestationMarkName);
        sheet.setLessons(lessons);

        assertEquals(teacherName, sheet.getTeacherName());
        assertEquals(currentAttestationMarkName, sheet.getCurrentAttestationMarkName());
        assertEquals(sheetAttestationMarkName, sheet.getSheetAttestationMarkName());
        assertEquals(lessons, sheet.getLessons());
    }

    @Test
    public void testLessonGettersAndSetters() {
        PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson = new PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson();
        String lessonDate = "2023-10-01";
        String homework = "Read par. 1";
        String lessonTypeName = "Lecture";
        String themePlanName = "Algebra";
        String markName = "5";

        lesson.setLessonDate(lessonDate);
        lesson.setHomework(homework);
        lesson.setLessonTypeName(lessonTypeName);
        lesson.setThemePlanName(themePlanName);
        lesson.setMarkName(markName);

        assertEquals(lessonDate, lesson.getLessonDate());
        assertEquals(homework, lesson.getHomework());
        assertEquals(lessonTypeName, lesson.getLessonTypeName());
        assertEquals(themePlanName, lesson.getThemePlanName());
        assertEquals(markName, lesson.getMarkName());
    }

    @Test
    public void testFullStructure() {
        PerformanceResponse response = new PerformanceResponse();
        PerformanceResponse.Plan plan = new PerformanceResponse.Plan();
        PerformanceResponse.Plan.Period period = new PerformanceResponse.Plan.Period();
        PerformanceResponse.Plan.Period.PlanCell planCell = new PerformanceResponse.Plan.Period.PlanCell();
        PerformanceResponse.Plan.Period.PlanCell.Attestation attestation = new PerformanceResponse.Plan.Period.PlanCell.Attestation();
        PerformanceResponse.Plan.Period.PlanCell.Sheet sheet = new PerformanceResponse.Plan.Period.PlanCell.Sheet();
        PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson = new PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson();

        lesson.setLessonDate("2023-10-01");
        lesson.setHomework("Read Chapter 1");
        lesson.setLessonTypeName("Lecture");
        lesson.setThemePlanName("Algebra Basics");
        lesson.setMarkName("5");

        sheet.setTeacherName("Dr. Smith");
        sheet.setCurrentAttestationMarkName("Pass");
        sheet.setSheetAttestationMarkName("B");
        sheet.setLessons(Collections.singletonList(lesson));

        attestation.setName("Final Exam");
        attestation.setMarkName("A");

        planCell.setRowIndex("1");
        planCell.setRowName("Math");
        planCell.setAttestation(attestation);
        planCell.setSheets(Collections.singletonList(sheet));

        period.setName("Semester 1");
        period.setPlanCells(Collections.singletonList(planCell));

        plan.setGroupName("Group A");
        plan.setPeriods(Collections.singletonList(period));

        response.setPlans(Collections.singletonList(plan));

        assertEquals("Group A", response.getPlans().get(0).getGroupName());
        assertEquals("Semester 1", response.getPlans().get(0).getPeriods().get(0).getName());
        assertEquals("Math", response.getPlans().get(0).getPeriods().get(0).getPlanCells().get(0).getRowName());
        assertEquals("A", response.getPlans().get(0).getPeriods().get(0).getPlanCells().get(0).getAttestation().getMarkName());
        assertEquals("Dr. Smith", response.getPlans().get(0).getPeriods().get(0).getPlanCells().get(0).getSheets().get(0).getTeacherName());
        assertEquals("5", response.getPlans().get(0).getPeriods().get(0).getPlanCells().get(0).getSheets().get(0).getLessons().get(0).getMarkName());
    }
}
