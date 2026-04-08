package org.cabinet.orthophonie.ui.main.sessions.new_session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.cabinet.orthophonie.data.patients.PatientRepository
import org.cabinet.orthophonie.data.sessions.SessionRepository
import org.cabinet.orthophonie.database.SessionRecord
import org.cabinet.orthophonie.ui.main.sessions.AttendanceStatus
import org.cabinet.orthophonie.utils.AppDispatchers
import kotlin.collections.emptyList
import kotlin.time.Clock
import kotlin.time.Instant

class NewSessionViewModel(
    private val patientRepository: PatientRepository,
    private val sessionRepository: SessionRepository,
    private val dispatchers: AppDispatchers,
    private val selectedSessionId: Long?,
    private val onBack: () -> Unit
) : ViewModel() {

    // 1. Un seul StateFlow pour tout le ViewModel
    private val _state = MutableStateFlow(
        NewSessionState(
            isLoading = true,
            // On initialise immédiatement avec ce que le Repo possède
            patients = patientRepository.patients.value ?: emptyList()
        )
    )
    val state: StateFlow<NewSessionState> = _state.asStateFlow()

    private var originalSession: SessionRecord? = null

    init {
        // Observer les patients en continu sans écraser le reste du state
        observePatients()
        // Charger la session si on est en mode édition
        loadInitialData()
    }

    private fun observePatients() {
        viewModelScope.launch {
            patientRepository.patients.collect { patientsList ->
                _state.update { it.copy(patients = patientsList ?: emptyList()) }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch(dispatchers.io) {
            try {
                if (selectedSessionId != null) {
                    val session = sessionRepository.getSessionById(selectedSessionId)
                    if (session != null) {
                        originalSession = session
                        val dateTime = Instant.parse(session.start_time)
                            .toLocalDateTime(TimeZone.currentSystemDefault())

                        val patient = patientRepository.getPatientById(session.patient_id)

                        _state.update { it.copy(
                            selectedPatient = patient,
                            selectedDate = dateTime.date,
                            selectedTime = dateTime.time,
                            sessionType = session.session_type,
                            attendanceStatus = session.attendance_status,
                            amount = session.amount.toString(),
                            paidAmount = session.paid_amount.toString(),
                            notes = session.notes ?: "",
                            isLoading = false
                        ) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = "Session introuvable") }
                    }
                } else {
                    // Mode création : on s'assure juste que le loading s'arrête
                    _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Erreur de chargement") }
            }
        }
    }

    fun onEvent(event: NewSessionEvents) {
        when (event) {
            is NewSessionEvents.OnPatientSelected -> _state.update { it.copy(selectedPatient = event.patient) }
            is NewSessionEvents.OnDateSelected -> _state.update { it.copy(selectedDate = event.date) }
            is NewSessionEvents.OnTimeSelected -> _state.update { it.copy(selectedTime = event.time) }
            is NewSessionEvents.OnSessionTypeChanged -> _state.update { it.copy(sessionType = event.type) }
            is NewSessionEvents.OnAmountChanged -> _state.update { it.copy(amount = event.amount) }
            is NewSessionEvents.OnPaidAmountChanged -> _state.update { it.copy(paidAmount = event.paidAmount) }
            is NewSessionEvents.OnNotesChanged -> _state.update { it.copy(notes = event.notes) }
            NewSessionEvents.OnBackClicked -> onBack()
            NewSessionEvents.OnSaveSession -> saveSession()
        }
    }

    private fun saveSession() {
        val s = _state.value
        // Validation simple
        if (s.selectedPatient == null || s.selectedDate == null || s.selectedTime == null) {
            _state.update { it.copy(error = "Champs obligatoires manquants") }
            return
        }

        viewModelScope.launch(dispatchers.io) {
            _state.update { it.copy(isSavingLoading = true, error = null) }
            try {
                val startTime = LocalDateTime(s.selectedDate, s.selectedTime)
                    .toInstant(TimeZone.currentSystemDefault())

                val paidAt =
                    if (s.paidAmount != "0.0" && (originalSession?.paid_amount != s.paidAmount.toDoubleOrNull()))
                        Clock.System.now().toString()
                    else null

                val session = SessionRecord(
                    id = originalSession?.id ?: 0,
                    patient_id = s.selectedPatient.id,
                    start_time = startTime.toString(),
                    duration_minutes = 45,
                    session_type = s.sessionType,
                    attendance_status = originalSession?.attendance_status ?: AttendanceStatus.PENDING,
                    notes = s.notes,
                    amount = s.amount.toDoubleOrNull() ?: 30.0,
                    paid_amount = s.paidAmount.toDoubleOrNull() ?: 0.0,
                    paid_at = paidAt
                )

                if (selectedSessionId != null) {
                    sessionRepository.updateSession(session)
                } else {
                    sessionRepository.insertSession(session)
                }

                withContext(dispatchers.main) {
                    onBack()
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSavingLoading = false, error = "Erreur lors de l'enregistrement") }
            }
        }
    }
}