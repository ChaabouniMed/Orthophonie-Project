package org.cabinet.orthophonie.ui.main.sessions

import com.arkivanov.decompose.ComponentContext

class SessionsComponent(
    componentContext: ComponentContext,
    val onAddSession: () -> Unit,
    val onSessionSelected: (sessionId: Long) -> Unit,
) :
    ComponentContext by componentContext