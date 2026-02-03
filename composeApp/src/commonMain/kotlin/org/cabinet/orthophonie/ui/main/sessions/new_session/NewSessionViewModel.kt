package org.cabinet.orthophonie.ui.main.sessions.new_session

import androidx.lifecycle.ViewModel
import org.cabinet.orthophonie.data.patients.PatientRepository
import org.cabinet.orthophonie.data.sessions.SessionRepository
import org.cabinet.orthophonie.utils.AppDispatchers

class NewSessionViewModel(
    private val sessionRepository: SessionRepository,
    private val patientRepository: PatientRepository,
    private val dispatchers: AppDispatchers,
    private val selectedPatientId: Long?,
    private val onBack: () -> Unit
) : ViewModel() {

}