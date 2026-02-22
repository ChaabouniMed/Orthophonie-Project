package org.cabinet.orthophonie.ui.main.home

import com.arkivanov.decompose.ComponentContext

class HomeComponent(
    componentContext: ComponentContext,
    val onAddSession: () -> Unit,
    val onSessionSelected: (sessionId: Long) -> Unit,
    ) :
    ComponentContext by componentContext