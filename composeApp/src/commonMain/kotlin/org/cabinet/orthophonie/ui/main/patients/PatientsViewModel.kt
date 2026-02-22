package org.cabinet.orthophonie.ui.main.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.cabinet.orthophonie.data.patients.PatientRepository
import org.cabinet.orthophonie.utils.AppDispatchers

class PatientsViewModel(
    private val repository: PatientRepository,
    private val dispatchers: AppDispatchers,
    private val onAddPatient: () -> Unit,
    private val onPatientSelected: (patientId: Long) -> Unit,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _statusFilter = MutableStateFlow(PatientStatusFilter.ACTIVE)

    val state: StateFlow<PatientsUiState> = combine(
        repository.patients,
        _searchQuery,
        _statusFilter
    ) { patientsList, query, status ->
        if (patientsList == null) {
            return@combine PatientsUiState(isLoading = true, searchQuery = query, selectedStatus = status)
        }

        val filteredByStatus = when (status) {
            PatientStatusFilter.ALL -> patientsList
            else -> patientsList.filter { it.status.name == status.name }
        }

        val filtered = if (query.isEmpty()) {
            filteredByStatus
        } else {
            filteredByStatus.filter {
                "${it.first_name} ${it.last_name}".contains(query, ignoreCase = true)
            }
        }

        PatientsUiState(
            isLoading = false,
            searchQuery = query,
            filteredPatients = filtered,
            selectedStatus = status
        )
    }
        // Ensure filtering and mapping happen on the IO dispatcher (off the Main thread)
        .flowOn(dispatchers.io)
        // Convert the cold Flow into a hot StateFlow for the UI
        .stateIn(
            scope = viewModelScope,
            // Keeps the flow active for 5 seconds after the last UI subscriber disconnects
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PatientsUiState(
                filteredPatients = emptyList(),
                isLoading = true,
                selectedStatus = PatientStatusFilter.ACTIVE
            )
        )

    fun onEvent(event: PatientsScreenEvents) {
        when (event) {
            is PatientsScreenEvents.OnSearchQueryChanged -> {
                _searchQuery.update { event.searchQuery }
            }
            is PatientsScreenEvents.OnStatusFilterChanged -> {
                _statusFilter.update { event.status }
            }
            is PatientsScreenEvents.OnPatientSelected -> {
                onPatientSelected(event.patientId)
            }
            PatientsScreenEvents.OnAddPatientClicked -> {
                onAddPatient()
            }
        }
    }
}