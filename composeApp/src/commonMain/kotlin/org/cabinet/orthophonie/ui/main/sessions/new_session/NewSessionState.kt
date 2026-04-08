package org.cabinet.orthophonie.ui.main.sessions.new_session

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.ui.main.sessions.AttendanceStatus
import org.cabinet.orthophonie.ui.main.sessions.SessionType

data class NewSessionState(
    val patients: List<PatientRecord> = emptyList(),
    val selectedPatient: PatientRecord? = null,
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val sessionType: SessionType = SessionType.NORMAL,
    val attendanceStatus: AttendanceStatus = AttendanceStatus.PENDING,
    val amount: String = "30.0",
    val paidAmount: String = "0.0",
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSavingLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)
