package com.example.irkpo_management.models;

import org.junit.Test;

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

        assertEquals(plans, response.plans);
    }

    @Test
    public void testPerformanceResponseWithNullPlans() {
        PerformanceResponse response = new PerformanceResponse();
        response.setPlans(null);

        assertNull(response.plans);
    }

    @Test
    public void testPlanGettersAndSetters() {
        PerformanceResponse.Plan plan = new PerformanceResponse.Plan();
        String groupName = "Group A";
        List<PerformanceResponse.Plan.Period> periods = Arrays.asList(new PerformanceResponse.Plan.Period());

        plan.groupName = groupName;
        plan.setPeriods(periods);

        assertEquals(groupName, plan.groupName);
        assertEquals(periods, plan.periods);
    }

    @Test
    public void testPeriodGettersAndSetters() {
        PerformanceResponse.Plan.Period period = new PerformanceResponse.Plan.Period();
        String name = "Semester 1";
        List<PerformanceResponse.Plan.Period.PlanCell> planCells = Arrays.asList(new PerformanceResponse.Plan.Period.PlanCell());

        period.name = name;
        period.setPlanCells(planCells);

        assertEquals(name, period.name);
        assertEquals(planCells, period.planCells);
    }

    @Test
    public void testPlanCellGettersAndSetters() {
        PerformanceResponse.Plan.Period.PlanCell planCell = new PerformanceResponse.Plan.Period.PlanCell();
        String rowIndex = "1";
        String rowName = "Math";
        PerformanceResponse.Plan.Period.PlanCell.Attestation attestation = new PerformanceResponse.Plan.Period.PlanCell.Attestation();
        List<PerformanceResponse.Plan.Period.PlanCell.Sheet> sheets = Arrays.asList(new PerformanceResponse.Plan.Period.PlanCell.Sheet());

        planCell.rowIndex = rowIndex;
        planCell.rowName = rowName;
        planCell.attestation = attestation;
        planCell.setSheets(sheets);

        assertEquals(rowIndex, planCell.rowIndex);
        assertEquals(rowName, planCell.rowName);
        assertEquals(attestation, planCell.attestation);
        assertEquals(sheets, planCell.sheets);
    }

    @Test
    public void testAttestationGettersAndSetters() {
        PerformanceResponse.Plan.Period.PlanCell.Attestation attestation = new PerformanceResponse.Plan.Period.PlanCell.Attestation();
        String name = "Final Exam";
        String markName = "A";

        attestation.name = name;
        attestation.markName = markName;

        assertEquals(name, attestation.name);
        assertEquals(markName, attestation.markName);
    }

    @Test
    public void testSheetGettersAndSetters() {
        PerformanceResponse.Plan.Period.PlanCell.Sheet sheet = new PerformanceResponse.Plan.Period.PlanCell.Sheet();
        String teacherName = "Dr. Smith";
        String currentAttestationMarkName = "Pass";
        String sheetAttestationMarkName = "B";
        List<PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson> lessons = Arrays.asList(new PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson());

        sheet.teacherName = teacherName;
        sheet.currentAttestationMarkName = currentAttestationMarkName;
        sheet.sheetAttestationMarkName = sheetAttestationMarkName;
        sheet.setLessons(lessons);

        assertEquals(teacherName, sheet.teacherName);
        assertEquals(currentAttestationMarkName, sheet.currentAttestationMarkName);
        assertEquals(sheetAttestationMarkName, sheet.sheetAttestationMarkName);
        assertEquals(lessons, sheet.lessons);
    }

    @Test
    public void testLessonGettersAndSetters() {
        PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson lesson = new PerformanceResponse.Plan.Period.PlanCell.Sheet.Lesson();
        String lessonDate = "2023-10-01";
        String homework = "Read par. 1";
        String lessonTypeName = "Lecture";
        String themePlanName = "Algebra";
        String markName = "5";

        lesson.lessonDate = lessonDate;
        lesson.homework = homework;
        lesson.lessonTypeName = lessonTypeName;
        lesson.themePlanName = themePlanName;
        lesson.markName = markName;

        assertEquals(lessonDate, lesson.lessonDate);
        assertEquals(homework, lesson.homework);
        assertEquals(lessonTypeName, lesson.lessonTypeName);
        assertEquals(themePlanName, lesson.themePlanName);
        assertEquals(markName, lesson.markName);
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

        lesson.lessonDate = "2023-10-01";
        lesson.homework = "Read Chapter 1";
        lesson.lessonTypeName = "Lecture";
        lesson.themePlanName = "Algebra Basics";
        lesson.markName = "5";

        sheet.teacherName = "Dr. Smith";
        sheet.currentAttestationMarkName = "Pass";
        sheet.sheetAttestationMarkName = "B";
        sheet.setLessons(Collections.singletonList(lesson));

        attestation.name = "Final Exam";
        attestation.markName = "A";

        planCell.rowIndex = "1";
        planCell.rowName = "Math";
        planCell.attestation = attestation;
        planCell.setSheets(Collections.singletonList(sheet));

        period.name = "Semester 1";
        period.setPlanCells(Collections.singletonList(planCell));

        plan.groupName = "Group A";
        plan.setPeriods(Collections.singletonList(period));

        response.setPlans(Collections.singletonList(plan));

        assertEquals("Group A", response.plans.get(0).groupName);
        assertEquals("Semester 1", response.plans.get(0).periods.get(0).name);
        assertEquals("Math", response.plans.get(0).periods.get(0).planCells.get(0).rowName);
        assertEquals("A", response.plans.get(0).periods.get(0).planCells.get(0).attestation.markName);
        assertEquals("Dr. Smith", response.plans.get(0).periods.get(0).planCells.get(0).sheets.get(0).teacherName);
        assertEquals("5", response.plans.get(0).periods.get(0).planCells.get(0).sheets.get(0).lessons.get(0).markName);
    }
}
