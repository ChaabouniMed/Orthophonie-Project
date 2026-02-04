package org.cabinet.orthophonie.ui.main.sessions.new_session

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.cabinet.orthophonie.database.PatientRecord

sealed interface NewSessionEvents {
    data class OnPatientSelected(val patient: PatientRecord) : NewSessionEvents
    data class OnDateSelected(val date: LocalDate) : NewSessionEvents
    data class OnTimeSelected(val time: LocalTime) : NewSessionEvents
    data class OnSessionTypeChanged(val type: String) : NewSessionEvents
    data class OnAmountChanged(val amount: String) : NewSessionEvents
    data class OnRecurringChanged(val isRecurring: Boolean) : NewSessionEvents
    data object OnSaveSession : NewSessionEvents
    data object OnBackClicked : NewSessionEvents
}