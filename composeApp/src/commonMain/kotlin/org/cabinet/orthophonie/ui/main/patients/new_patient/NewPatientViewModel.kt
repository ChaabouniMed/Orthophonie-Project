package org.cabinet.orthophonie.ui.main.patients.new_patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.cabinet.orthophonie.data.patients.PatientRepository
import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.utils.AppDispatchers
import kotlin.time.Clock

class NewPatientViewModel(
    private val repository: PatientRepository,
    private val dispatchers: AppDispatchers,
    private val selectedPatientId: Long?,
    private val onBack: () -> Unit
) : ViewModel() {

    private val _state = MutableStateFlow(NewPatientState(
        isLoading = true
    ))
    val state = _state.asStateFlow()

    // Keep track of the original record if editing
    private var originalPatient: PatientRecord? = null

    init {
        loadPatientData()
    }

    private fun loadPatientData() {
        if (selectedPatientId == null) {
            _state.update { it.copy(isLoading = false) }
            return
        } else {
            viewModelScope.launch(dispatchers.io) {
                val patient = repository.getPatientById(selectedPatientId)
                if (patient != null) {
                    originalPatient = patient
                    _state.update {
                        it.copy(
                            firstName = patient.first_name,
                            lastName = patient.last_name,
                            dateBirth = patient.date_birth,
                            contactParent = patient.contact_parent,
                            school = patient.school,
                            schoolClass = patient.school_class,
                            status = patient.status,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Patient not found") }
                }
            }
        }
    }

    fun onEvent(event: NewPatientEvents) {
        when (event) {
            // Using .update for thread-safe UI state changes
            is NewPatientEvents.OnFirstNameChanged -> _state.update { it.copy(firstName = event.value, error = null) }
            is NewPatientEvents.OnLastNameChanged -> _state.update { it.copy(lastName = event.value, error = null) }
            is NewPatientEvents.OnDateBirthChanged -> _state.update { it.copy(dateBirth = event.value) }
            is NewPatientEvents.OnContactParentChanged -> _state.update { it.copy(contactParent = event.value, error = null) }
            is NewPatientEvents.OnSchoolChanged -> _state.update { it.copy(school = event.value) }
            is NewPatientEvents.OnClassChanged -> _state.update { it.copy(schoolClass = event.value) }
            is NewPatientEvents.OnStatusChanged -> _state.update { it.copy(status = event.value) }
            NewPatientEvents.OnBackClicked -> onBack()
            NewPatientEvents.OnSavePatient -> savePatient()
        }
    }

    private fun savePatient() {
        val s = _state.value

        // 1. Validation Logic
        if (s.firstName.isBlank() || s.lastName.isBlank() || s.contactParent.isBlank()) {
            _state.update { it.copy(error = "Required fields * are missing") }
            return
        }

        viewModelScope.launch(dispatchers.io) {
            _state.update { it.copy(isSavingLoading = true, error = null) }
            try {
                // 2. Prepare the object
                val patient = PatientRecord(
                    id = originalPatient?.id ?: 0,
                    first_name = s.firstName.trim(),
                    last_name = s.lastName.trim(),
                    date_birth = s.dateBirth,
                    contact_parent = s.contactParent.trim(),
                    school = s.school?.trim(),
                    school_class = s.schoolClass?.trim(),
                    status = s.status,
                    creation_date = originalPatient?.creation_date ?: Clock.System.now().toString()
                )

                // 3. Upsert (Update or Insert)
                if (originalPatient == null) {
                    repository.insertPatient(patient)
                } else {
                    repository.updatePatient(patient)
                }

                _state.update { it.copy(isSavingLoading = false, isSaved = true) }

                // 4. Navigation on Main Thread
                withContext(dispatchers.main) {
                    onBack()
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSavingLoading = false, error = e.message) }
            }
        }
    }
}