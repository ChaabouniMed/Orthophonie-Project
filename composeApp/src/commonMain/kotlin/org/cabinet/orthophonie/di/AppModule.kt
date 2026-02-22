package org.cabinet.orthophonie.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.cabinet.orthophonie.data.patients.PatientDao
import org.cabinet.orthophonie.data.patients.PatientRepository
import org.cabinet.orthophonie.data.sessions.SessionDao
import org.cabinet.orthophonie.data.sessions.SessionRepository
import org.cabinet.orthophonie.database.AppDatabase
import org.cabinet.orthophonie.ui.main.home.HomeViewModel
import org.cabinet.orthophonie.ui.main.patients.PatientsViewModel
import org.cabinet.orthophonie.ui.main.patients.new_patient.NewPatientViewModel
import org.cabinet.orthophonie.ui.main.sessions.SessionsViewModel
import org.cabinet.orthophonie.ui.main.sessions.new_session.NewSessionViewModel
import org.cabinet.orthophonie.utils.AppDispatchers
import org.cabinet.orthophonie.utils.AppDispatchersImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

private val coreModule = module {
    single { AppDispatchersImpl() } bind AppDispatchers::class
    single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
}

private val daoModule = module {
    single { PatientDao(get(), get<AppDatabase>().patientQueries) }
    single { SessionDao(get(), get<AppDatabase>().sessionQueries) }
}

private val repoModule = module {
    single { PatientRepository(get(), get(), get()) }
    single { SessionRepository(get(), get(), get()) }
}

private val viewModelModule = module {
    viewModel { (onAddPatient: () -> Unit, onPatientSelected: (Long) -> Unit) ->
        PatientsViewModel(
            get(),
            get(),
            onAddPatient,
            onPatientSelected
        )
    }
    viewModel { (selectedPatientId: Long?, onBack: () -> Unit) ->
        NewPatientViewModel(
            get(),
            get(),
            selectedPatientId,
            onBack
        )
    }
    single { (onAddSession: () -> Unit, onSessionSelected: (Long) -> Unit) ->
        SessionsViewModel(
            get(),
            get(),
            get(),
            onAddSession,
            onSessionSelected
        )
    }
    viewModel { (selectedSessionId: Long?, onBack: () -> Unit) ->
        NewSessionViewModel(
            get(),
            get(),
            get(),
            selectedSessionId,
            onBack
        )
    }
    viewModel { HomeViewModel() }
}

expect val dbModule: Module

val koinModules = listOf(coreModule, daoModule, repoModule, viewModelModule, dbModule)
