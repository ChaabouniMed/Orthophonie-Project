package org.cabinet.orthophonie.ui.main.sessions

import org.cabinet.orthophonie.database.GetSessions

data class SessionsUiState(
    val sessions: List<GetSessions> = emptyList(),
    val todaySessions: List<GetSessions> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
