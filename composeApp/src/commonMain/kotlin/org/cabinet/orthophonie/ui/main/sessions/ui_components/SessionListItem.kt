package org.cabinet.orthophonie.ui.main.sessions.ui_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.cabinet.orthophonie.database.GetSessions
import org.cabinet.orthophonie.ui.main.sessions.AttendanceStatus
import org.cabinet.orthophonie.utils.toDisplayState
import org.cabinet.orthophonie.utils.toDropdownMenuItemColor
import org.cabinet.orthophonie.utils.toDropdownMenuItemIcon

@Composable
fun SessionListItem(
    session: GetSessions,
    onClick: () -> Unit,
    onAttendanceStatusChanged: (AttendanceStatus) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    // On utilise remember pour ne pas recalculer la logique à chaque recomposition
    val display = remember(session) { session.toDisplayState() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar()

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(display.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(session.session_type.name, color = Color.Gray, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(8.dp))

                    AttendanceStatusBadge(
                        status = session.attendance_status,
                        bgColor = display.statusBgColor,
                        textColor = display.statusColor,
                        expanded = showMenu,
                        onToggle = { showMenu = it },
                        onStatusSelected = onAttendanceStatusChanged
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Date & Time
                InfoRow(Icons.Default.CalendarMonth, display.dateStr, Color.Gray)
                InfoRow(Icons.Default.Schedule, display.timeStr, Color(0xFF2196F3), isBold = true)

                // Payment Info
                display.paymentInfo?.let { info ->
                    Text(
                        text = info.message,
                        color = info.color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileAvatar() {
    Box(
        modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFE3F2FD)),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Person, null, tint = Color(0xFF2196F3), modifier = Modifier.size(32.dp))
    }
}

@Composable
private fun AttendanceStatusBadge(
    status: AttendanceStatus,
    bgColor: Color,
    textColor: Color,
    expanded: Boolean,
    onToggle: (Boolean) -> Unit,
    onStatusSelected: (AttendanceStatus) -> Unit
) {
    Box {
        Surface(
            color = bgColor,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.clickable { onToggle(true) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = status.toDropdownMenuItemIcon(),
                    contentDescription = null,
                    tint = textColor
                )
                Text(
                    text = status.name,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    color = textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { onToggle(false) }) {
            AttendanceStatus.entries.forEach { entry ->
                DropdownMenuItem(
                    text = { Text(entry.name) },
                    onClick = {
                        onStatusSelected(entry)
                        onToggle(false)
                    },
                    colors = MenuDefaults.itemColors(textColor = entry.toDropdownMenuItemColor()),
                    leadingIcon = {
                        Icon(
                            imageVector = entry.toDropdownMenuItemIcon(),
                            contentDescription = null,
                            tint = entry.toDropdownMenuItemColor()
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String, color: Color, isBold: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = color,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}