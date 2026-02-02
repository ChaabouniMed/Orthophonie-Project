package org.cabinet.orthophonie

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.cabinet.orthophonie.di.koinModules
import org.cabinet.orthophonie.ui.OrthophonieApp
import org.cabinet.orthophonie.ui.RootComponent
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    val isKoinStarted = remember {
        try {
            startKoin {
                modules(koinModules)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    val lifecycle = remember { LifecycleRegistry() }
    val root = remember {
        RootComponent(componentContext = DefaultComponentContext(lifecycle))
    }

    OrthophonieApp(component = root)
}