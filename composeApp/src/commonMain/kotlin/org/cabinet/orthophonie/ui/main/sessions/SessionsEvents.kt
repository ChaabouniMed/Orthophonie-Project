package org.cabinet.orthophonie.ui.main.sessions

sealed interface SessionsEvents {
    data class OnSessionClicked(val sessionId: Long) : SessionsEvents
    data object OnAddSessionClicked : SessionsEvents
}
