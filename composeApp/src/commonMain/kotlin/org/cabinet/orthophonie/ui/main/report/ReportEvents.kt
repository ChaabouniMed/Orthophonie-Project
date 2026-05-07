package org.cabinet.orthophonie.ui.main.report

sealed interface ReportEvents {
    data class OnPatientSelected(val patientId: Long?) : ReportEvents
    data class OnPeriodChanged(val period: ReportPeriod) : ReportEvents
    data object RefreshStats : ReportEvents
}
