package org.cabinet.orthophonie

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.cabinet.orthophonie.di.koinModules
import org.cabinet.orthophonie.ui.OrthophonieApp
import org.cabinet.orthophonie.ui.RootComponent
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(koinModules)
    }

    application {
        val lifecycle = remember { LifecycleRegistry() }
        val root = remember {
            RootComponent(componentContext = DefaultComponentContext(lifecycle))
        }

        Window(
            onCloseRequest = ::exitApplication,
            title = "Orthophonie",
        ) {
            OrthophonieApp(component = root)
        }
    }
}