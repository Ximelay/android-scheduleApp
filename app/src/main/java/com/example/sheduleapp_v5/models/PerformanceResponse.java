package com.example.sheduleapp_v5.models;

import java.util.List;

public class PerformanceResponse {
    private List<Plan> plans;

    public static class Plan {
        private String groupName;
        private List<Period> periods;

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public List<Period> getPeriods() {
            return periods;
        }

        public void setPeriods(List<Period> periods) {
            this.periods = periods;
        }

        public static class Period {
            private String name;
            private List<PlanCell> planCells;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<PlanCell> getPlanCells() {
                return planCells;
            }

            public void setPlanCells(List<PlanCell> planCells) {
                this.planCells = planCells;
            }

            public static class PlanCell {
                private String rowIndex;
                private String rowName;
                private Attestation attestation;
                private List<Sheet> sheets;

                public String getRowIndex() {
                    return rowIndex;
                }

                public void setRowIndex(String rowIndex) {
                    this.rowIndex = rowIndex;
                }

                public String getRowName() {
                    return rowName;
                }

                public void setRowName(String rowName) {
                    this.rowName = rowName;
                }

                public Attestation getAttestation() {
                    return attestation;
                }

                public void setAttestation(Attestation attestation) {
                    this.attestation = attestation;
                }

                public List<Sheet> getSheets() {
                    return sheets;
                }

                public void setSheets(List<Sheet> sheets) {
                    this.sheets = sheets;
                }

                public static class Attestation {
                    private String name;
                    private String markName;

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getMarkName() {
                        return markName;
                    }

                    public void setMarkName(String markName) {
                        this.markName = markName;
                    }
                }

                public static class Sheet {
                    private String teacherName;
                    private String currentAttestationMarkName;
                    private String sheetAttestationMarkName;
                    private List<Lesson> lessons;

                    public String getTeacherName() {
                        return teacherName;
                    }

                    public void setTeacherName(String teacherName) {
                        this.teacherName = teacherName;
                    }

                    public String getCurrentAttestationMarkName() {
                        return currentAttestationMarkName;
                    }

                    public void setCurrentAttestationMarkName(String currentAttestationMarkName) {
                        this.currentAttestationMarkName = currentAttestationMarkName;
                    }

                    public String getSheetAttestationMarkName() {
                        return sheetAttestationMarkName;
                    }

                    public void setSheetAttestationMarkName(String sheetAttestationMarkName) {
                        this.sheetAttestationMarkName = sheetAttestationMarkName;
                    }

                    public List<Lesson> getLessons() {
                        return lessons;
                    }

                    public void setLessons(List<Lesson> lessons) {
                        this.lessons = lessons;
                    }

                    public static class Lesson {
                        private String lessonDate;
                        private String homework;
                        private String lessonTypeName;
                        private String themePlanName;
                        private String markName;

                        public String getLessonDate() {
                            return lessonDate;
                        }

                        public void setLessonDate(String lessonDate) {
                            this.lessonDate = lessonDate;
                        }

                        public String getHomework() {
                            return homework;
                        }

                        public void setHomework(String homework) {
                            this.homework = homework;
                        }

                        public String getLessonTypeName() {
                            return lessonTypeName;
                        }

                        public void setLessonTypeName(String lessonTypeName) {
                            this.lessonTypeName = lessonTypeName;
                        }

                        public String getThemePlanName() {
                            return themePlanName;
                        }

                        public void setThemePlanName(String themePlanName) {
                            this.themePlanName = themePlanName;
                        }

                        public String getMarkName() {
                            return markName;
                        }

                        public void setMarkName(String markName) {
                            this.markName = markName;
                        }
                    }
                }
            }
        }
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }
}
