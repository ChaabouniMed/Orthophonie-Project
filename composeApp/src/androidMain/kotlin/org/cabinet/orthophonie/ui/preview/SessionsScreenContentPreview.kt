package org.cabinet.orthophonie.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.cabinet.orthophonie.database.GetSessions
import org.cabinet.orthophonie.ui.main.sessions.AttendanceStatus
import org.cabinet.orthophonie.ui.main.sessions.SessionType
import org.cabinet.orthophonie.ui.main.sessions.SessionsScreenContent
import org.cabinet.orthophonie.ui.main.sessions.SessionsUiState
import org.cabinet.orthophonie.ui.theme.AppTheme
import kotlin.time.Clock

@Preview(showBackground = true)
@Composable
fun SessionsScreenContentPreview() {
    AppTheme {
        SessionsScreenContent(
            uiState = SessionsUiState(
                sessions = listOf(
                    GetSessions(
                        id = 1L,
                        patient_id = 1L,
                        start_time = Clock.System.now().toString(),
                        duration_minutes = 45,
                        session_type = SessionType.NORMAL,
                        attendance_status = AttendanceStatus.ABSENT,
                        is_recurring = true,
                        notes = "Séance de langage oral",
                        amount = 60.0,
                        paid_amount = 60.0,
                        paid_at = Clock.System.now().toString(),
                        first_name = "Ali",
                        last_name = "Ben Salah"
                    ),
                    GetSessions(
                        id = 2L,
                        patient_id = 2L,
                        start_time = Clock.System.now().toString(),
                        duration_minutes = 30,
                        session_type = SessionType.NORMAL,
                        attendance_status = AttendanceStatus.ABSENT,
                        is_recurring = false,
                        notes = null,
                        amount = 50.0,
                        paid_amount = 0.0,
                        paid_at = null,
                        first_name = "Sara",
                        last_name = "Trabelsi"
                    ),
                    GetSessions(
                        id = 3L,
                        patient_id = 3L,
                        start_time = Clock.System.now().toString(),
                        duration_minutes = 60,
                        session_type = SessionType.NORMAL,
                        attendance_status = AttendanceStatus.ABSENT,
                        is_recurring = false,
                        notes = "Premier rendez-vous",
                        amount = 80.0,
                        paid_amount = null,
                        paid_at = null,
                        first_name = "Youssef",
                        last_name = "Mejri"
                    )
                ),
                isLoading = false
            ),
            onEvent = {}
        )
    }
}