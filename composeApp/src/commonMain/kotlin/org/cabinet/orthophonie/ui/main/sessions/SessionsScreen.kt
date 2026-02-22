package org.cabinet.orthophonie.ui.main.sessions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import org.cabinet.orthophonie.ui.main.sessions.ui_components.SessionListItem
import org.cabinet.orthophonie.utils.toDropdownMenuItemColor
import org.cabinet.orthophonie.utils.toDropdownMenuItemIcon
import org.cabinet.orthophonie.utils.toLocalDateTime

@Composable
fun SessionsScreen(
    onAddSession: () -> Unit,
    viewModel: SessionsViewModel
) {
    val uiState by viewModel.state.collectAsState()

    SessionsScreenContent(
        uiState = uiState,
        onEvent = { event ->
            if (event is SessionsEvents.OnAddSessionClicked) {
                onAddSession()
            } else {
                viewModel.onEvent(event)
            }
        }
    )
}

@Composable
fun SessionsScreenContent(
    uiState: SessionsUiState,
    onEvent: (SessionsEvents) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(SessionsEvents.OnAddSessionClicked) },
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nouvelle séance")
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Séances",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            FiltersRow(uiState, onEvent)

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2196F3))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Affichage du Calendrier (Condition unique)
                    if (uiState.sessionUserUiState.showCalendar) {
                        item {
                            CompactDatePicker(
                                state = datePickerState,
                                onDateSelected = { onEvent(SessionsEvents.OnDateFilterChanged(it)) }
                            )
                        }
                    }

                    // 2. Liste des Sessions
                    if (uiState.filteredSessions.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxHeight(0.1f)
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Aucune séance trouvée", color = Color.Gray)
                            }
                        }
                    } else {
                        items(uiState.filteredSessions, key = { it.id }) { session ->
                            SessionListItem(
                                session = session,
                                onClick = { onEvent(SessionsEvents.OnSessionClicked(session.id)) },
                                onAttendanceStatusChanged = { status ->
                                    onEvent(SessionsEvents.OnSessionAttendanceChanged(session.id, status))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FiltersRow(
    uiState: SessionsUiState,
    onEvent: (SessionsEvents) -> Unit
) {
    var showPatientMenu by remember { mutableStateOf(false) }
    var showSessionTypeMenu by remember { mutableStateOf(false) }
    var showAttendanceStatusMenu by remember { mutableStateOf(false) }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        // Patient Filter
        item {
            Box {
                FilterChip(
                    text = uiState.patients.find { it.id == uiState.sessionUserUiState.selectedPatientId }?.let { "${it.first_name} ${it.last_name}" } ?: "Patient",
                    isSelected = uiState.sessionUserUiState.selectedPatientId != null,
                    onClick = { showPatientMenu = true }
                )
                DropdownMenu(
                    expanded = showPatientMenu,
                    onDismissRequest = { showPatientMenu = false }
                ) {
                    DropdownMenuItem(text = { Text("All") }, onClick = { onEvent(SessionsEvents.OnPatientFilterChanged(null)); showPatientMenu = false })
                    uiState.patients.forEach { patient ->
                        DropdownMenuItem(
                            text = { Text("${patient.first_name} ${patient.last_name}") },
                            onClick = {
                                onEvent(SessionsEvents.OnPatientFilterChanged(patient.id))
                                showPatientMenu = false
                            }
                        )
                    }
                }
            }
        }

        // Calendar Filter
        item {
            Box {
                FilterChip(
                    text = "Calendrier",
                    isSelected = uiState.sessionUserUiState.showCalendar,
                    onClick = { onEvent(SessionsEvents.OnShowCalendarChanged(!uiState.sessionUserUiState.showCalendar)) }
                )
            }
        }

        // Type Filter
        item {
            Box {
                FilterChip(
                    text = uiState.sessionUserUiState.selectedSessionType?.name ?: "Session Type",
                    isSelected = uiState.sessionUserUiState.selectedSessionType != null,
                    onClick = { showSessionTypeMenu = true }
                )
                DropdownMenu(
                    expanded = showSessionTypeMenu,
                    onDismissRequest = { showSessionTypeMenu = false }
                ) {
                    DropdownMenuItem(text = { Text("All") }, onClick = { onEvent(SessionsEvents.OnTypeFilterChanged(null)); showSessionTypeMenu = false })
                    SessionType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                onEvent(SessionsEvents.OnTypeFilterChanged(if (uiState.sessionUserUiState.selectedSessionType == type) null else type))
                                showSessionTypeMenu = false
                            }
                        )
                    }
                }
            }
        }

        // Attendance Filter
        item {
            Box {
                FilterChip(
                    text = uiState.sessionUserUiState.selectedAttendanceStatus?.name ?: "Attendance",
                    isSelected = uiState.sessionUserUiState.selectedAttendanceStatus != null,
                    onClick = { showAttendanceStatusMenu = true }
                )
                DropdownMenu(
                    expanded = showAttendanceStatusMenu,
                    onDismissRequest = { showAttendanceStatusMenu = false }
                ) {
                    DropdownMenuItem(text = { Text("All") }, onClick = { onEvent(SessionsEvents.OnAttendanceFilterChanged(null)); showAttendanceStatusMenu = false })
                    AttendanceStatus.entries.forEach { attendanceStatus ->
                        DropdownMenuItem(
                            text = { Text(attendanceStatus.name) },
                            onClick = {
                                onEvent(SessionsEvents.OnAttendanceFilterChanged(if (uiState.sessionUserUiState.selectedAttendanceStatus == attendanceStatus) null else attendanceStatus))
                                showAttendanceStatusMenu = false
                            },
                            colors = MenuDefaults.itemColors(textColor = attendanceStatus.toDropdownMenuItemColor()),
                            leadingIcon = {
                                Icon(
                                    imageVector = attendanceStatus.toDropdownMenuItemIcon(),
                                    contentDescription = null,
                                    tint = attendanceStatus.toDropdownMenuItemColor()
                                )
                            }
                        )
                    }
                }
            }
        }

        // Pending Payment Filter
        item {
            FilterChip(
                text = "Non payé",
                isSelected = uiState.sessionUserUiState.onlyPendingPayment,
                onClick = { onEvent(SessionsEvents.OnPendingPaymentFilterChanged(!uiState.sessionUserUiState.onlyPendingPayment)) }
            )
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) Color(0xFFE3F2FD) else Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .height(36.dp),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isSelected) Color(0xFF2196F3) else Color.Gray,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactDatePicker(
    state: DatePickerState,
    onDateSelected: (LocalDate) -> Unit
) {
    LaunchedEffect(state.selectedDateMillis) {
        state.selectedDateMillis?.let { millis ->
            val selectedDate = millis.toLocalDateTime().date
            onDateSelected(selectedDate)
        }
    }

    Card(
        modifier = Modifier.padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        DatePicker(
            state = state,
            title = null,
            headline = null,
            showModeToggle = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}