package org.cabinet.orthophonie.ui.main.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.cabinet.orthophonie.data.patients.PatientRepository
import org.cabinet.orthophonie.data.sessions.SessionRepository
import org.cabinet.orthophonie.data.sessions.matchAndAdjustDate
import org.cabinet.orthophonie.utils.AppDispatchers

class SessionsViewModel(
    private val sessionRepository: SessionRepository,
    private val patientRepository: PatientRepository,
    private val dispatchers: AppDispatchers,
    private val onAddSession: () -> Unit,
    private val onSessionSelected: (sessionId: Long) -> Unit,
) : ViewModel() {

    private val _sessionUserUiState = MutableStateFlow(SessionUserUiState())

    val state: StateFlow<SessionsUiState> = combine(
        sessionRepository.sessions,
        sessionRepository.todaySessions,
        patientRepository.patients,
        _sessionUserUiState
    ) { sessions, todaySessions, patients, sessionUserUiState ->

        val filteredSessions = sessions?.mapNotNull { session ->
            val matchPatient = sessionUserUiState.selectedPatientId == null || session.patient_id == sessionUserUiState.selectedPatientId
            val matchType = sessionUserUiState.selectedSessionType == null || session.session_type == sessionUserUiState.selectedSessionType
            val matchAttendance = sessionUserUiState.selectedAttendanceStatus == null || session.attendance_status == sessionUserUiState.selectedAttendanceStatus
            val matchPending = !sessionUserUiState.onlyPendingPayment || (session.attendance_status == AttendanceStatus.PRESENT && (session.paid_amount ?: 0.0) < (session.amount ?: 30.0))
            
            val userSelectedDate = sessionUserUiState.selectedDate
            val sessionAfterDateFilter = if (userSelectedDate != null) {
                session.matchAndAdjustDate(userSelectedDate)
            } else {
                session
            }

            if (matchPatient && matchType && matchAttendance && matchPending && sessionAfterDateFilter != null) {
                sessionAfterDateFilter
            } else {
                null
            }
        }

        SessionsUiState(
            sessions = sessions ?: emptyList(),
            todaySessions = todaySessions ?: emptyList(),
            filteredSessions = filteredSessions ?: emptyList(),
            patients = patients ?: emptyList(),
            isLoading = false,
            sessionUserUiState = sessionUserUiState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionsUiState(
            sessions = sessionRepository.sessions.value ?: emptyList(),
            todaySessions = sessionRepository.todaySessions.value ?: emptyList(),
            filteredSessions = emptyList(),
            patients = patientRepository.patients.value ?: emptyList(),
            sessionUserUiState = SessionUserUiState(),
            isLoading = sessionRepository.sessions.value == null
        )
    )

    fun onEvent(event: SessionsEvents) {
        when (event) {
            is SessionsEvents.OnSessionClicked -> { onSessionSelected(event.sessionId) }
            SessionsEvents.OnAddSessionClicked -> { onAddSession() }
            is SessionsEvents.OnPatientFilterChanged -> _sessionUserUiState.update { it.copy(selectedPatientId = event.patientId) }
            is SessionsEvents.OnTypeFilterChanged -> _sessionUserUiState.update { it.copy(selectedSessionType = event.type) }
            is SessionsEvents.OnAttendanceFilterChanged -> _sessionUserUiState.update { it.copy(selectedAttendanceStatus = event.status) }
            is SessionsEvents.OnPendingPaymentFilterChanged -> _sessionUserUiState.update { it.copy(onlyPendingPayment = event.onlyPending) }
            is SessionsEvents.OnSessionAttendanceChanged -> {
                viewModelScope.launch { sessionRepository.updateSessionAttendance(event.sessionId, event.attendanceStatus)  }
            }
            is SessionsEvents.OnShowCalendarChanged -> _sessionUserUiState.update {
                it.copy(
                    showCalendar = event.showCalendar,
                    selectedDate = if (!event.showCalendar) null else it.selectedDate
                )
            }
            is SessionsEvents.OnDateFilterChanged -> _sessionUserUiState.update { it.copy(selectedDate = event.date) }
        }
    }
}
