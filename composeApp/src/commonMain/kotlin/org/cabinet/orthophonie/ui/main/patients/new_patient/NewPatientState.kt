package org.cabinet.orthophonie.ui.main.patients.new_patient

import org.cabinet.orthophonie.ui.main.patients.PatientStatus

data class NewPatientState(
    val firstName: String = "",
    val lastName: String = "",
    val dateBirth: String? = null,
    val contactParent: String = "",
    val school: String? = null,
    val schoolClass: String ? = null,
    val status: PatientStatus = PatientStatus.ACTIVE,
    val isLoading: Boolean = false,
    val isSavingLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)