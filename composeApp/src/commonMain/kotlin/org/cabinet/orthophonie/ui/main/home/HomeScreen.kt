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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import orthophonie.composeapp.generated.resources.Res.string
import orthophonie.composeapp.generated.resources.*

@Composable
fun HomeScreen(

) {
    HomeScreenContent()
}

@Composable
fun HomeScreenContent(

) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        HeaderSection()
        SummaryGrid()
        AppointmentsSection()
        QuickActionsSection()
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
                text = "Good Morning",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )
            Text(
                text = "Here's your practice overview",
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
fun SummaryGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard(
                title = stringResource(string.total_patients),
                icon = Icons.Default.Groups,
                iconBgColor = Color(0xFFE3F2FD),
                iconColor = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = stringResource(string.today_appointments),
                icon = Icons.Default.EventAvailable,
                iconBgColor = Color(0xFFE0F2F1),
                iconColor = Color(0xFF009688),
                modifier = Modifier.weight(1f)
            )
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
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
fun AppointmentsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(string.today_appointments),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { }) {
                Text(stringResource(string.see_all), color = Color(0xFF2196F3))
            }
        }

        repeat(3) {
            AppointmentItem()
        }
    }
}

@Composable
fun AppointmentItem() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "9:30",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3),
                    fontSize = 18.sp
                )
                Text(
                    text = "AM",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "John Smith",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Consultation",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(string.confirmed),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = Color(0xFF2196F3),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}

@Composable
fun QuickActionsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(string.quick_actions),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionButton(
                label = stringResource(string.new_patient),
                icon = Icons.Default.Add,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = stringResource(string.new_patient),
                icon = Icons.Default.PersonAdd,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(32.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}