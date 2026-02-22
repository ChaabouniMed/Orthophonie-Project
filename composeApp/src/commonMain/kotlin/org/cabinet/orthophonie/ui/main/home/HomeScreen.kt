package org.cabinet.orthophonie.ui.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.cabinet.orthophonie.database.GetSessions
import org.cabinet.orthophonie.ui.main.sessions.AttendanceStatus
import org.cabinet.orthophonie.ui.main.sessions.SessionsEvents
import org.cabinet.orthophonie.ui.main.sessions.SessionsViewModel
import org.cabinet.orthophonie.ui.main.sessions.ui_components.SessionListItem
import org.jetbrains.compose.resources.stringResource
import orthophonie.composeapp.generated.resources.Res.string
import orthophonie.composeapp.generated.resources.good_morning
import orthophonie.composeapp.generated.resources.monthly_revenue
import orthophonie.composeapp.generated.resources.new_patient
import orthophonie.composeapp.generated.resources.new_session
import orthophonie.composeapp.generated.resources.pending_payments
import orthophonie.composeapp.generated.resources.practice_overview
import orthophonie.composeapp.generated.resources.quick_actions
import orthophonie.composeapp.generated.resources.see_all
import orthophonie.composeapp.generated.resources.today_sessions
import orthophonie.composeapp.generated.resources.total_patients
import orthophonie.composeapp.generated.resources.total_sessions

@Composable
fun HomeScreen(
    sessionViewModel: SessionsViewModel,
    navigateToPatients: () -> Unit,
    navigateToSessions: () -> Unit,
    onNewPatient: () -> Unit,
    onNewSession: () -> Unit
) {
    val sessionsUiState by sessionViewModel.state.collectAsState()
    HomeScreenContent(
        todaySessions = sessionsUiState.todaySessions,
        onSessionAttendanceStatusChanged = { sessionId, attendanceStatus ->
            sessionViewModel.onEvent(SessionsEvents.OnSessionAttendanceChanged(sessionId, attendanceStatus))
        },
        navigateToPatients = navigateToPatients,
        navigateToSessions = navigateToSessions,
        onSessionSelected = { sessionViewModel.onEvent(SessionsEvents.OnSessionClicked(it)) },
        onNewPatient = onNewPatient,
        onNewSession = onNewSession
    )
}

@Composable
fun HomeScreenContent(
    todaySessions: List<GetSessions>,
    onSessionAttendanceStatusChanged: (sessionId: Long, attendanceStatus: AttendanceStatus) -> Unit,
    navigateToPatients: () -> Unit = {},
    navigateToSessions: () -> Unit = {},
    onSessionSelected: (sessionId: Long) -> Unit,
    onNewPatient: () -> Unit = {},
    onNewSession: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { HeaderSection() }
        item {
            SummaryGrid(
                navigateToPatients,
                navigateToSessions
            )
        }
        item { SessionsSection(
            todaySessions,
            navigateToSessions,
            onSessionSelected = onSessionSelected,
            onAttendanceStatusChanged = onSessionAttendanceStatusChanged
        ) }
        item { QuickActionsSection(
            onNewPatient,
            onNewSession
            ) }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(string.good_morning),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )
            Text(
                text = stringResource(string.practice_overview),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        IconButton(
            onClick = { },
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .size(48.dp)
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
        }
    }
}

@Composable
fun SummaryGrid(
    navigateToPatients: () -> Unit,
    navigateToSessions: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard(
                title = stringResource(string.total_patients),
                icon = Icons.Default.Groups,
                iconBgColor = Color(0xFFE3F2FD),
                iconColor = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            ) {
                navigateToPatients()
            }
            SummaryCard(
                title = stringResource(string.total_sessions),
                icon = Icons.Default.EventAvailable,
                iconBgColor = Color(0xFFE0F2F1),
                iconColor = Color(0xFF009688),
                modifier = Modifier.weight(1f)
            ) {
                navigateToSessions()
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard(
                title = stringResource(string.pending_payments),
                icon = Icons.Default.Schedule,
                iconBgColor = Color(0xFFFFF3E0),
                iconColor = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = stringResource(string.monthly_revenue),
                icon = Icons.Default.AttachMoney,
                iconBgColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBgColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SessionsSection(
    todaySessions: List<GetSessions>,
    navigateToSessions: () -> Unit,
    onAttendanceStatusChanged: (sessionId: Long, attendanceStatus: AttendanceStatus) -> Unit,
    onSessionSelected: (sessionId: Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(string.today_sessions),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { navigateToSessions()}) {
                Text(stringResource(string.see_all), color = Color(0xFF2196F3))
            }
        }

        todaySessions.map {
            SessionListItem(
                session = it,
                onClick = { onSessionSelected(it.id) },
                onAttendanceStatusChanged = { attendanceStatus ->
                    onAttendanceStatusChanged(it.id, attendanceStatus)
                }
            )
        }
    }
}

@Composable
fun QuickActionsSection(
    onNewPatient: () -> Unit,
    onNewSession: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(string.quick_actions),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionButton(
                label = stringResource(string.new_patient),
                icon = Icons.Default.PersonAdd,
                modifier = Modifier.weight(1f),
                onClick = onNewPatient
            )
            QuickActionButton(
                label = stringResource(string.new_session),
                icon = Icons.Default.Add,
                modifier = Modifier.weight(1f),
                onClick = onNewSession
            )
        }
    }
}

@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp),
            onClick = onClick
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}