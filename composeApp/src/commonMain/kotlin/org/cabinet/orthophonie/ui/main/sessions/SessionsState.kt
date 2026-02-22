package org.cabinet.orthophonie.ui.main.sessions

import androidx.compose.ui.graphics.Color
import kotlinx.datetime.LocalDate
import org.cabinet.orthophonie.database.GetSessions
import org.cabinet.orthophonie.database.PatientRecord

data class SessionsUiState(
    val sessions: List<GetSessions> = emptyList(),
    val todaySessions: List<GetSessions> = emptyList(),
    val filteredSessions: List<GetSessions> = emptyList(),
    val patients: List<PatientRecord> = emptyList(),
    val sessionUserUiState: SessionUserUiState = SessionUserUiState(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class SessionUserUiState(
    // Filters
    val selectedPatientId: Long? = null,
    val selectedSessionType: SessionType? = null,
    val selectedAttendanceStatus: AttendanceStatus? = null,
    val showCalendar: Boolean = false,
    val selectedDate: LocalDate? = null,
    val onlyPendingPayment: Boolean = false
)

enum class AttendanceStatus {
    PENDING,
    PRESENT,
    ABSENT,
    CANCELED
}

enum class SessionType {
    NORMAL,
    BILAN
}

data class SessionDisplayState(
    val fullName: String,
    val dateStr: String,
    val timeStr: String,
    val statusColor: Color,
    val statusBgColor: Color,
    val paymentInfo: PaymentDisplayInfo?
)

data class PaymentDisplayInfo(
    val message: String,
    val color: Color
)
