package org.cabinet.orthophonie.ui.main.patients

import androidx.lifecycle.ViewModel
import org.cabinet.orthophonie.data.patients.PatientRepository
import org.cabinet.orthophonie.utils.AppDispatchers

class PatientsViewModel(
    private val repository: PatientRepository,
    private val dispatchers: AppDispatchers
): ViewModel() {
}