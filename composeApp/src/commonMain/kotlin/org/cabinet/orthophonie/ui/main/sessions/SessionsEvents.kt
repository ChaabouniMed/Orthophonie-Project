package org.cabinet.orthophonie.ui.main.sessions

import kotlinx.datetime.LocalDate

sealed interface SessionsEvents {
    data class OnSessionClicked(val sessionId: Long) : SessionsEvents
    data object OnAddSessionClicked : SessionsEvents
    data class OnSessionAttendanceChanged(val sessionId: Long, val attendanceStatus: AttendanceStatus) : SessionsEvents
    data class OnPatientFilterChanged(val patientId: Long?) : SessionsEvents
    data class OnShowCalendarChanged(val showCalendar: Boolean) : SessionsEvents
    data class OnDateFilterChanged(val date: LocalDate?) : SessionsEvents
    data class OnTypeFilterChanged(val type: SessionType?) : SessionsEvents
    data class OnAttendanceFilterChanged(val status: AttendanceStatus?) : SessionsEvents
    data class OnPendingPaymentFilterChanged(val onlyPending: Boolean) : SessionsEvents
}
