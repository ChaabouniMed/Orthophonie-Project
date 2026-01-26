package org.cabinet.orthophonie

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.cabinet.orthophonie.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Orthophonie",
    ) {
        App()
    }
}