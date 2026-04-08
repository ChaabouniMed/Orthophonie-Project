package org.cabinet.orthophonie.data.sessions

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.cabinet.orthophonie.database.GetSessions
import org.cabinet.orthophonie.ui.main.sessions.AttendanceStatus
import org.koin.compose.koinInject
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Adjusts the date of a past, pending, recurring session to its next upcoming occurrence.
 * If the session doesn't meet these criteria, it's returned unchanged.
 */
fun GetSessions.adjustForRecurrence(): GetSessions {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    return try {
        val sessionDateTime = Instant.parse(this.start_time).toLocalDateTime(TimeZone.currentSystemDefault())

        // Check if the session is in the past, recurring, and still pending
        if (this.is_recurring == true &&
            this.attendance_status == AttendanceStatus.PENDING &&
            sessionDateTime.date < today
        ) {
            val sessionDayOfWeek = sessionDateTime.dayOfWeek
            // Calculate days to add to get to the next occurrence of the same day of the week
            var daysToAdd = sessionDayOfWeek.isoDayNumber - today.dayOfWeek.isoDayNumber
            if (daysToAdd < 0) {
                daysToAdd += 7
            }
            
            val nextDate = today.plus(daysToAdd, DateTimeUnit.DAY)
            val newLocalDateTime = LocalDateTime(nextDate, sessionDateTime.time)
            val newInstant = newLocalDateTime.toInstant(TimeZone.currentSystemDefault())
            this.copy(start_time = newInstant.toString())
        } else {
            this
        }
    } catch (e: Exception) {
        this // Return original session if parsing fails
    }
}

/**
 * Checks if a session matches a target date (directly or via recurrence, if via recurrence we must check if )
 * and returns the session with an adjusted start_time if it's a recurrence on that day.
 */
fun GetSessions.matchAndAdjustDate(
    targetDate: LocalDate
): GetSessions? {
    val sessionsRepository: SessionRepository = koinInject()
    return try {
        val sessionDateTime = Instant.parse(this.start_time)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        
        val isSameDate = sessionDateTime.date == targetDate
        val isRecurringOnSameDayAndNoOtherSessionWithStatusDifferentOfPendingIsThere = //w manensech nesnaa new session kif tetbadel l status ml Pending l haja okhra w hiya recurrent
            this.is_recurring == true &&
                    sessionDateTime.dayOfWeek == targetDate.dayOfWeek &&
                    sessionsOfTargetDate?.none { it.id != this.id && it.attendance_status != AttendanceStatus.PENDING } == true
        
        when {
            isSameDate -> this
            isRecurringOnSameDay -> {
                val newLocalDateTime = LocalDateTime(targetDate, sessionDateTime.time)
                val newInstant = newLocalDateTime.toInstant(TimeZone.currentSystemDefault())
                this.copy(start_time = newInstant.toString())
            }
            else -> null
        }
    } catch (e: Exception) {
        null
    }
}
