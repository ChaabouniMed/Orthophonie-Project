package org.cabinet.orthophonie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.arkivanov.decompose.defaultComponentContext
import org.cabinet.orthophonie.ui.OrthophonieApp
import org.cabinet.orthophonie.ui.RootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val componentContext = remember { defaultComponentContext() }
            val root = remember(componentContext) {
                RootComponent(componentContext = componentContext)
            }
            OrthophonieApp(component = root)
        }
    }
}