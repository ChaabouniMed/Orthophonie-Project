package org.cabinet.orthophonie.ui.main.patients

import com.arkivanov.decompose.ComponentContext

class PatientsComponent(
    componentContext: ComponentContext,
    val onAddPatient: () -> Unit,
    val onPatientSelected: (Long) -> Unit,
    ) :
    ComponentContext by componentContext