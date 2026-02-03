package org.cabinet.orthophonie.ui.main.sessions.new_session

import com.arkivanov.decompose.ComponentContext

class NewSessionComponent(
    componentContext: ComponentContext,
    val selectedSessionId: Long?,
    val onBack: () -> Unit
    ) :
    ComponentContext by componentContext