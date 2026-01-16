package com.example.irkpo_management.models

class PerformanceResponse {
    @JvmField
    var plans: MutableList<Plan?>? = null

    fun setPlans(plans: MutableList<Plan?>?) {
        this.plans = plans
    }

    class Plan {
        @JvmField
        var groupName: String? = null
        @JvmField
        var periods: MutableList<Period?>? = null

        fun setPeriods(periods: MutableList<Period?>?) {
            this.periods = periods
        }

        class Period {
            @JvmField
            var name: String? = null
            @JvmField
            var planCells: MutableList<PlanCell?>? = null

            fun setPlanCells(planCells: MutableList<PlanCell?>?) {
                this.planCells = planCells
            }

            class PlanCell {
                @JvmField
                var rowIndex: String? = null
                @JvmField
                var rowName: String? = null
                @JvmField
                var attestation: Attestation? = null
                @JvmField
                var sheets: MutableList<Sheet?>? = null

                fun setSheets(sheets: MutableList<Sheet?>?) {
                    this.sheets = sheets
                }

                class Attestation {
                    @JvmField
                    var name: String? = null
                    @JvmField
                    var markName: String? = null
                }

                class Sheet {
                    @JvmField
                    var teacherName: String? = null
                    @JvmField
                    var currentAttestationMarkName: String? = null
                    @JvmField
                    var sheetAttestationMarkName: String? = null
                    @JvmField
                    var lessons: MutableList<Lesson?>? = null

                    fun setLessons(lessons: MutableList<Lesson?>?) {
                        this.lessons = lessons
                    }

                    class Lesson {
                        @JvmField
                        var lessonDate: String? = null
                        @JvmField
                        var homework: String? = null
                        @JvmField
                        var lessonTypeName: String? = null
                        @JvmField
                        var themePlanName: String? = null
                        @JvmField
                        var markName: String? = null
                    }
                }
            }
        }
    }
}
