package org.cabinet.orthophonie.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.cabinet.orthophonie.database.GetSessions
import org.cabinet.orthophonie.ui.main.sessions.AttendanceStatus
import org.cabinet.orthophonie.ui.main.sessions.PaymentDisplayInfo
import org.cabinet.orthophonie.ui.main.sessions.SessionDisplayState
import kotlin.time.Instant

fun GetSessions.toDisplayState(): SessionDisplayState {
    // Formatage Date & Heure
    val date = this.start_time.parseToDate()
    val time = this.start_time.parseToTime()

    // Logique des couleurs du statut
    val (statusBg, statusTint) = when (this.attendance_status) {
        AttendanceStatus.PRESENT -> Color(0xFFE8F5E9) to attendance_status.toDropdownMenuItemColor()
        AttendanceStatus.PENDING -> Color(0xFFE3F2FD) to attendance_status.toDropdownMenuItemColor()
        else -> Color(0xFFFFEBEE) to attendance_status.toDropdownMenuItemColor()
    }

    // Logique de paiement
    val amount = this.amount ?: 30.0
    val paid = this.paid_amount ?: 0.0
    val paymentInfo = if (this.attendance_status == AttendanceStatus.PRESENT) {
        when {
            paid < amount -> PaymentDisplayInfo(
                "Reste à payer: ${amount - paid} DT",
                Color(0xFFF44336)
            )

            paid > amount -> PaymentDisplayInfo(
                "Paiement supérieur: ${paid - amount} DT",
                Color(0xFF2196F3)
            )

            else -> PaymentDisplayInfo("Bien payé : $paid DT", Color(0xFF4CAF50))
        }
    } else null

    return SessionDisplayState(
        fullName = "${this.first_name} ${this.last_name}",
        dateStr = date,
        timeStr = time,
        statusBgColor = statusBg,
        statusColor = statusTint,
        paymentInfo = paymentInfo
    )
}

fun AttendanceStatus.toDropdownMenuItemColor(): Color =
    when (this) {
        AttendanceStatus.PENDING -> Color(0xFF2196F3)
        AttendanceStatus.PRESENT -> Color(0xFF4CAF50)
        else -> Color(0xFFF44336)
    }

@Composable
fun AttendanceStatus.toDropdownMenuItemIcon(): ImageVector = when (this) {
    AttendanceStatus.PENDING -> Icons.Default.Schedule
    AttendanceStatus.PRESENT -> Icons.Default.CheckCircle
    AttendanceStatus.ABSENT -> Icons.Default.Cancel
    AttendanceStatus.CANCELED -> Icons.Default.Block
}
