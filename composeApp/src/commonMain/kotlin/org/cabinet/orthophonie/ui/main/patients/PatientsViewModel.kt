package org.cabinet.orthophonie.ui.main.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.cabinet.orthophonie.data.patients.PatientRepository
import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.utils.AppDispatchers

class PatientsViewModel(
    private val repository: PatientRepository,
    private val dispatchers: AppDispatchers,
    private val onAddPatient: () -> Unit,
    private val onPatientSelected: (Long) -> Unit,
) : ViewModel() {
    private val _state = MutableStateFlow(PatientsUiState())
    val state: StateFlow<PatientsUiState>
        get() = _state

    lateinit var patients: List<PatientRecord>

    init {
        viewModelScope.launch(dispatchers.io) {
            repository.getPatients().collect { patientsList ->
                patients = patientsList
                _state.value = _state.value.copy(
                    filteredPatients = patients,
                    isLoading = false
                )
            }
        }
    }

    fun onEvent(event: PatientsScreenEvents) {
        when (event) {
            is PatientsScreenEvents.OnPatientSelected -> {
                onPatientSelected(event.patientId)
            }
            is PatientsScreenEvents.OnSearchQueryChanged -> {
                _state.value = _state.value.copy(
                    searchQuery = event.searchQuery,
                    filteredPatients = if (event.searchQuery.isEmpty()) patients else patients.filter {
                        "${it.first_name} ${it.last_name}".contains(
                            event.searchQuery,
                            ignoreCase = true
                        )
                    }
                )
            }
            is PatientsScreenEvents.OnAddPatientClicked -> {
                onAddPatient()
            }
        }
    }
}