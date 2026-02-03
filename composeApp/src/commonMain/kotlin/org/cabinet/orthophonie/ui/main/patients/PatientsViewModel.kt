package org.cabinet.orthophonie.ui.main.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
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

    // 1. Local state for search query only
    //Il faut séparer le state coming from bdd from le state coming from l'ui ou l'utilisateur
    private val _searchQuery = MutableStateFlow("")

    // 2. Reactive UI State
    // Combines the database flow from SqlDelight with the local search query flow
    val state: StateFlow<PatientsUiState> = combine(
        repository.getPatients(),
        _searchQuery
    ) { patientsList, query ->
        // Filtering is done in-memory using the latest list emitted by the database
        val filtered = if (query.isEmpty()) {
            patientsList
        } else {
            patientsList.filter {
                "${it.first_name} ${it.last_name}".contains(query, ignoreCase = true)
            }
        }

        PatientsUiState(
            isLoading = false,
            searchQuery = query,
            filteredPatients = filtered
        )
    }
        // Emit loading state when the flow collection starts
        .onStart { emit(PatientsUiState(isLoading = true)) }
        // Ensure filtering and mapping happen on the IO dispatcher (off the Main thread)
        .flowOn(dispatchers.io)
        // Convert the cold Flow into a hot StateFlow for the UI
        .stateIn(
            scope = viewModelScope,
            // Keeps the flow active for 5 seconds after the last UI subscriber disconnects
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PatientsUiState(isLoading = true)
        )

    /**
     * Handle UI events using the MVI (Model-View-Intent) pattern
     */
    fun onEvent(event: PatientsScreenEvents) {
        when (event) {
            is PatientsScreenEvents.OnSearchQueryChanged -> {
                // Update the search query source.
                // Using .update is safer than .value = ... in concurrent environments.
                _searchQuery.update { event.searchQuery }
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