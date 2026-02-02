package org.cabinet.orthophonie.ui.main.patients.new_patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.cabinet.orthophonie.data.patients.PatientRepository
import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.utils.AppDispatchers
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class NewPatientViewModel(
    private val repository: PatientRepository,
    private val dispatchers: AppDispatchers,
    private val selectedPatientId: Long?,
    private val onBack: () -> Unit
) : ViewModel() {

    private val _state = MutableStateFlow(NewPatientState())
    val state = _state.asStateFlow()

    private var selectedPatient: PatientRecord? = null

    init {
        if (selectedPatientId != null) {
            _state.update { it.copy(
                isLoading = true
            )
            }
            viewModelScope.launch(dispatchers.io) {
                val patient = repository.getPatientById(selectedPatientId)
                patient?.let { patient ->
                    selectedPatient = patient
                    _state.update { it.copy(
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
                }
            }
        }
    }

    fun onEvent(event: NewPatientEvents) {
        when (event) {
            is NewPatientEvents.OnFirstNameChanged -> _state.update { it.copy(firstName = event.value) }
            is NewPatientEvents.OnLastNameChanged -> _state.update { it.copy(lastName = event.value) }
            is NewPatientEvents.OnDateBirthChanged -> _state.update { it.copy(dateBirth = event.value) }
            is NewPatientEvents.OnContactParentChanged -> _state.update { it.copy(contactParent = event.value) }
            is NewPatientEvents.OnSchoolChanged -> _state.update { it.copy(school = event.value) }
            is NewPatientEvents.OnClassChanged -> _state.update { it.copy(schoolClass = event.value) }
            is NewPatientEvents.OnStatusChanged -> _state.update { it.copy(status = event.value) }
            NewPatientEvents.OnBackClicked -> onBack()
            NewPatientEvents.OnSavePatient -> savePatient()
        }
    }

    private fun savePatient() {
        val currentState = _state.value
        if (currentState.firstName.isBlank() || currentState.lastName.isBlank()  || currentState.contactParent.isBlank()) {
            _state.update { it.copy(error = "Le prénom, le nom et le contact parent sont obligatoires") }
            return
        }

        viewModelScope.launch(dispatchers.io) {
            _state.update { it.copy(isSavingLoading = true) }
            try {
                val now = selectedPatient?.creation_date ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
                val patient = PatientRecord(
                    id = selectedPatient?.id ?: 0,
                    first_name = currentState.firstName,
                    last_name = currentState.lastName,
                    date_birth = currentState.dateBirth,
                    contact_parent = currentState.contactParent,
                    school = currentState.school,
                    school_class = currentState.schoolClass,
                    status = currentState.status,
                    creation_date = now
                )
                if (selectedPatient == null)
                    repository.insertPatient(patient)
                else
                    repository.updatePatient(patient)

                _state.update { it.copy(isSavingLoading = false, isSaved = true) }
                viewModelScope.launch(dispatchers.main) {
                    onBack()
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSavingLoading = false, error = e.message) }
            }
        }
    }
}