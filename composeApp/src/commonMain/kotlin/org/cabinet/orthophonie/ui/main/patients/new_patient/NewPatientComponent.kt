package org.cabinet.orthophonie.ui.main.patients.new_patient

import com.arkivanov.decompose.ComponentContext

class NewPatientComponent(
    componentContext: ComponentContext,
    val selectedPatientId: Long?,
    val onBack: () -> Unit
) : ComponentContext by componentContext