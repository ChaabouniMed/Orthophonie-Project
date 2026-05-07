package org.cabinet.orthophonie.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import org.cabinet.orthophonie.ui.main.home.HomeScreen
import org.cabinet.orthophonie.ui.main.patients.PatientsScreen
import org.cabinet.orthophonie.ui.main.patients.new_patient.NewPatientScreen
import org.cabinet.orthophonie.ui.main.report.ReportScreen
import org.cabinet.orthophonie.ui.main.sessions.SessionsScreen
import org.cabinet.orthophonie.ui.main.sessions.new_session.NewSessionScreen
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun MainScreens(
    component: MainComponent,
) {
    var selectedScreen by rememberSaveable { mutableStateOf(BottomNavigationItem.HOME) }

    val onHomeTabClicked = {
        selectedScreen = BottomNavigationItem.HOME
        component.onHomeTabClicked()
    }
    val onSessionsTabClicked = {
        selectedScreen = BottomNavigationItem.Sessions
        component.onSessionsTabClicked()
    }
    val onPatientsTabClicked = {
        selectedScreen = BottomNavigationItem.PATIENTS
        component.onPatientsTabClicked()
    }
    val onReportTabClicked = {
        selectedScreen = BottomNavigationItem.REPORTS
        component.onReportTabClicked()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = selectedScreen,
                onHomeTabClicked = onHomeTabClicked,
                onSessionsTabClicked = onSessionsTabClicked,
                onPatientsTabClicked = onPatientsTabClicked,
                onReportTabClicked = onReportTabClicked
            )
        }
    ) { padding ->
        MainScreensContent(
            component = component,
            onTotalPatientsClicked = onPatientsTabClicked,
            onSessionsTabClicked = onSessionsTabClicked,
            padding = padding,
        )
    }
}

@Composable
fun MainScreensContent(
    component: MainComponent,
    onTotalPatientsClicked: () -> Unit,
    onSessionsTabClicked: () -> Unit,
    padding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Children(stack = component.childStack) {
            when (val child = it.instance) {
                is MainComponent.Child.HomeChild -> HomeScreen(
                    sessionViewModel = koinInject {
                        parametersOf(
                            child.component.onAddSession,
                            child.component.onSessionSelected
                        )
                    },
                    navigateToPatients = onTotalPatientsClicked,
                    navigateToSessions = onSessionsTabClicked,
                    onNewPatient = { component.onNewPatientClicked(null) },
                    onNewSession = { component.onNewSessionClicked(null) }
                )

                is MainComponent.Child.SessionsChild -> SessionsScreen(
                    onAddSession = child.component.onAddSession,
                    viewModel = koinInject {
                        parametersOf(
                            child.component.onAddSession,
                            child.component.onSessionSelected
                        )
                    }
                )

                is MainComponent.Child.ReportChild -> ReportScreen()
                is MainComponent.Child.PatientsChild -> PatientsScreen(
                    viewModel = koinInject {
                        parametersOf(
                            child.component.onAddPatient,
                            child.component.onPatientSelected
                        )
                    }
                )

                is MainComponent.Child.NewPatientChild -> NewPatientScreen(
                    viewModel = koinInject {
                        parametersOf(
                            child.component.selectedPatientId,
                            child.component.onBack
                        )
                    }
                )

                is MainComponent.Child.NewSessionChild -> NewSessionScreen(
                    viewModel = koinInject {
                        parametersOf(
                            child.component.selectedSessionId,
                            child.component.onBack
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedScreen: BottomNavigationItem,
    onHomeTabClicked: () -> Unit,
    onSessionsTabClicked: () -> Unit,
    onPatientsTabClicked: () -> Unit,
    onReportTabClicked: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            selected = selectedScreen == BottomNavigationItem.HOME,
            onClick = { onHomeTabClicked() }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            selected = selectedScreen == BottomNavigationItem.Sessions,
            onClick = { onSessionsTabClicked() }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Groups, contentDescription = null) },
            selected = selectedScreen == BottomNavigationItem.PATIENTS,
            onClick = { onPatientsTabClicked() }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            selected = selectedScreen == BottomNavigationItem.REPORTS,
            onClick = { onReportTabClicked() }
        )
    }
}

enum class BottomNavigationItem {
    HOME, Sessions, PATIENTS, REPORTS
}