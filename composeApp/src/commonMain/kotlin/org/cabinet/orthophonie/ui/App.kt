package org.cabinet.orthophonie.ui

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import org.cabinet.orthophonie.ui.main.MainScreens
import org.cabinet.orthophonie.ui.theme.AppTheme

@Composable
fun OrthophonieApp(
    component: RootComponent,
) {
    AppTheme {
        Children(
            stack = component.childStack,
            animation = stackAnimation(fade()),
        ) {
            when (val child = it.instance) {
                is RootComponent.Child.MainChild -> MainScreens(child.component)
            }
        }
    }
}
