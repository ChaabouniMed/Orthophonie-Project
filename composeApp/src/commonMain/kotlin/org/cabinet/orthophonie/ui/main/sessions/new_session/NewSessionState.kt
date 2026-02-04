package org.cabinet.orthophonie.ui.main.sessions.new_session

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.cabinet.orthophonie.database.PatientRecord

data class NewSessionState(
    val patients: List<PatientRecord> = emptyList(),
    val selectedPatient: PatientRecord? = null,
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val sessionType: String = "NORMAL",
    val amount: String = "30.0",
    val isRecurring: Boolean = false,
    val isLoading: Boolean = false,
    val isSavingLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)
