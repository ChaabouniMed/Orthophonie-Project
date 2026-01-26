package org.cabinet.orthophonie.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import org.cabinet.orthophonie.ui.main.MainComponent

class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private val stack =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Main,
            handleBackButton = true,
            childFactory = ::createChild,
        )
    val childStack: Value<ChildStack<*, Child>> = stack

    private fun createChild(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Main -> Child.MainChild(MainComponent(componentContext))
        }

    sealed class Child {
        class MainChild(val component: MainComponent) : Child()
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object Main : Config()
    }

}