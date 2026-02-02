package org.cabinet.orthophonie.di

import org.cabinet.orthophonie.data.patients.PatientDao
import org.cabinet.orthophonie.data.patients.PatientRepository
import org.cabinet.orthophonie.database.AppDatabase
import org.cabinet.orthophonie.ui.main.home.HomeViewModel
import org.cabinet.orthophonie.ui.main.patients.PatientsViewModel
import org.cabinet.orthophonie.ui.main.patients.new_patient.NewPatientViewModel
import org.cabinet.orthophonie.utils.AppDispatchers
import org.cabinet.orthophonie.utils.AppDispatchersImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

private val coreModule = module {
    single { AppDispatchersImpl() } bind AppDispatchers::class
}

private val daoModule = module {
    single { PatientDao(get(), get<AppDatabase>().patientQueries) }
}

private val repoModule = module {
    single { PatientRepository(get(), get()) }
}

private val viewModelModule = module {
    viewModel { (onAddPatient: () -> Unit, onPatientSelected: (Long) -> Unit) -> PatientsViewModel(get(), get(), onAddPatient, onPatientSelected) }
    viewModel { (selectedPatientId: Long? ,onBack: () -> Unit) -> NewPatientViewModel(get(), get(), selectedPatientId, onBack) }
    viewModel { HomeViewModel() } //naaml nafs ella9ta
}

expect val dbModule: Module

val koinModules = listOf(coreModule, daoModule, repoModule, viewModelModule, dbModule)
