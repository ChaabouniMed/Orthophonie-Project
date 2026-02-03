package org.cabinet.orthophonie.ui.main.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.cabinet.orthophonie.data.sessions.SessionRepository
import org.cabinet.orthophonie.utils.AppDispatchers

class SessionsViewModel(
    private val sessionRepository: SessionRepository,
    private val dispatchers: AppDispatchers,
    private val onAddSession: () -> Unit,
    private val onSessionSelected: (sessionId: Long) -> Unit,
) : ViewModel() {

    val state: StateFlow<SessionsUiState> = combine(
        sessionRepository.sessions,
        sessionRepository.todaySessions
    ) { all, today ->
        SessionsUiState(
            sessions = all,
            todaySessions = today,
            isLoading = false
        )
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionsUiState(
                sessions = sessionRepository.sessions.value,
                todaySessions = sessionRepository.todaySessions.value,
                isLoading = sessionRepository.sessions.value.isEmpty()
            )
        )

    fun onEvent(event: SessionsEvents) {
        when (event) {
            is SessionsEvents.OnSessionClicked -> {
                onSessionSelected(event.sessionId)
            }
            SessionsEvents.OnAddSessionClicked -> {
                onAddSession()
            }
        }
    }
}
