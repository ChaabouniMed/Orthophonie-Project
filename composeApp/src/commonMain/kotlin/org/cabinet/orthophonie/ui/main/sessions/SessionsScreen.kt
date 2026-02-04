package org.cabinet.orthophonie.ui.main.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.cabinet.orthophonie.database.GetSessions

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

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2196F3))
                }
            } else if (uiState.sessions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucune séance prévue", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.sessions) { session ->
                        SessionListItem(
                            session = session,
                            onClick = { onEvent(SessionsEvents.OnSessionClicked(session.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SessionListItem(
    session: GetSessions,
    onClick: () -> Unit
) {
    val dateTime = Instant.parse(session.start_time).toLocalDateTime(TimeZone.currentSystemDefault())
    val dateStr = "${dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${dateTime.day}, ${dateTime.year}"
    
    val hour = if (dateTime.hour > 12) dateTime.hour - 12 else if (dateTime.hour == 0) 12 else dateTime.hour
    val amPm = if (dateTime.hour >= 12) "PM" else "AM"
    val timeStr = "${hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')} $amPm"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${session.first_name} ${session.last_name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1A1C1E)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = session.session_type ?: "Consultation",
                        color = Color.Gray,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = session.attendance_status ?: "Confirmed",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = Color(0xFF2196F3),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dateStr,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = timeStr,
                            color = Color(0xFF2196F3),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(
//                        Icons.Default.CalendarMonth,
//                        contentDescription = null,
//                        tint = Color.LightGray,
//                        modifier = Modifier.size(18.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = dateStr,
//                        color = Color.Gray,
//                        fontSize = 14.sp
//                    )
//                    Spacer(modifier = Modifier.width(16.dp))
//                    Icon(
//                        Icons.Default.Schedule,
//                        contentDescription = null,
//                        tint = Color(0xFF2196F3),
//                        modifier = Modifier.size(18.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = timeStr,
//                        color = Color(0xFF2196F3),
//                        fontSize = 14.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}
