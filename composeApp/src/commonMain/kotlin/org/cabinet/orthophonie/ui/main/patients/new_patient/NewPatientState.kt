package org.cabinet.orthophonie.ui.main.patients.new_patient

data class NewPatientState(
    val firstName: String = "",
    val lastName: String = "",
    val dateBirth: String? = null,
    val contactParent: String = "",
    val school: String? = null,
    val schoolClass: String ? = null,
    val status: String = "Active", // Default to Active
    val isLoading: Boolean = false,
    val isSavingLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)