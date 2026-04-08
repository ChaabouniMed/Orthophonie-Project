package org.cabinet.orthophonie.ui.main.sessions.new_session

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.ui.main.sessions.SessionType

sealed interface NewSessionEvents {
    data class OnPatientSelected(val patient: PatientRecord) : NewSessionEvents
    data class OnDateSelected(val date: LocalDate) : NewSessionEvents
    data class OnTimeSelected(val time: LocalTime) : NewSessionEvents
    data class OnSessionTypeChanged(val type: SessionType) : NewSessionEvents
    data class OnAmountChanged(val amount: String) : NewSessionEvents
    data class OnPaidAmountChanged(val paidAmount: String) : NewSessionEvents
    data class OnNotesChanged(val notes: String) : NewSessionEvents
    data object OnSaveSession : NewSessionEvents
    data object OnBackClicked : NewSessionEvents
}