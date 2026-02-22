package org.cabinet.orthophonie.ui.main.patients

import org.cabinet.orthophonie.database.PatientRecord

data class PatientsUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val filteredPatients: List<PatientRecord> = emptyList(),
    val selectedStatus: PatientStatusFilter = PatientStatusFilter.ACTIVE
)

enum class PatientStatusFilter(val text: String) {
    ALL("All"), ACTIVE("Active"), ARCHIVED("Archived")
}

enum class PatientStatus {
    ACTIVE, ARCHIVED
}
