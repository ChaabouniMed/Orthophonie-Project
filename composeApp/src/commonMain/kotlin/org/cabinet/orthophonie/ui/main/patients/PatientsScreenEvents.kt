package org.cabinet.orthophonie.ui.main.patients

sealed interface PatientsScreenEvents {
    data class OnPatientSelected(val patientId: Long) : PatientsScreenEvents
    data class OnSearchQueryChanged(val searchQuery: String) : PatientsScreenEvents
    data class OnStatusFilterChanged(val status: PatientStatusFilter) : PatientsScreenEvents
    object OnAddPatientClicked : PatientsScreenEvents
}