package org.cabinet.orthophonie.ui.main.patients

import org.cabinet.orthophonie.database.PatientRecord

data class PatientsUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val filteredPatients: List<PatientRecord> = emptyList()
    )
