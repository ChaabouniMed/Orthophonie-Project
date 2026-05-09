package org.cabinet.orthophonie.ui.main.report

sealed interface ReportEvents {
    data class OnPatientSelected(val patientId: Long?) : ReportEvents
    data class OnPeriodChanged(val period: ReportPeriod) : ReportEvents
    data class OnMonthChanged(val month: Int) : ReportEvents
    data class OnYearChanged(val year: Int) : ReportEvents
    data object RefreshStats : ReportEvents
}
