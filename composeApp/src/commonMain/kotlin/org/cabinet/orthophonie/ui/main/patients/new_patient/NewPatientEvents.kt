package org.cabinet.orthophonie.ui.main.patients.new_patient

import org.cabinet.orthophonie.ui.main.patients.PatientStatus

sealed interface NewPatientEvents {
    data class OnFirstNameChanged(val value: String) : NewPatientEvents
    data class OnLastNameChanged(val value: String) : NewPatientEvents
    data class OnDateBirthChanged(val value: String) : NewPatientEvents
    data class OnContactParentChanged(val value: String) : NewPatientEvents
    data class OnSchoolChanged(val value: String) : NewPatientEvents
    data class OnClassChanged(val value: String) : NewPatientEvents
    data class OnStatusChanged(val value: PatientStatus) : NewPatientEvents
    data object OnSavePatient : NewPatientEvents
    data object OnBackClicked : NewPatientEvents
}