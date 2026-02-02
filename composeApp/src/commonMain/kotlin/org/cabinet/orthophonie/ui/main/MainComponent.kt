package org.cabinet.orthophonie.ui.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.ui.main.calendar.CalendarComponent
import org.cabinet.orthophonie.ui.main.home.HomeComponent
import org.cabinet.orthophonie.ui.main.patients.PatientsComponent
import org.cabinet.orthophonie.ui.main.patients.new_patient.NewPatientComponent
import org.cabinet.orthophonie.ui.main.report.ReportComponent

class MainComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    private val stack =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Home,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    val childStack: Value<ChildStack<*, Child>> = stack

    private fun createChild(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Home -> Child.HomeChild(HomeComponent(componentContext))
            is Config.Calendar -> Child.CalendarChild(CalendarComponent(componentContext))
            is Config.Report -> Child.ReportChild(ReportComponent(componentContext))
            is Config.Patients -> Child.PatientsChild(
                PatientsComponent(
                    componentContext,
                    onAddPatient = { onNewPatientClicked(null) },
                    onPatientSelected = { onNewPatientClicked(it) },
                ))
            is Config.NewPatient -> Child.NewPatientChild(
                NewPatientComponent(
                    componentContext = componentContext,
                    selectedPatientId = config.selectedPatientId,
                    onBack = { navigation.pop() }
                )
            )
        }

    fun onHomeTabClicked() {
        navigation.bringToFront(Config.Home)
    }

    fun onCalendarTabClicked() {
        navigation.bringToFront(Config.Calendar)
    }

    fun onPatientsTabClicked() {
        navigation.bringToFront(Config.Patients)
    }

    fun onReportTabClicked() {
        navigation.bringToFront(Config.Report)
    }

    fun onNewPatientClicked(selectedPatientId: Long?) {
        navigation.bringToFront(Config.NewPatient(selectedPatientId))
    }

    sealed class Child {
        class HomeChild(val component: HomeComponent) : Child()
        class CalendarChild(val component: CalendarComponent) : Child()
        class PatientsChild(val component: PatientsComponent) : Child()
        class ReportChild(val component: ReportComponent) : Child()
        class NewPatientChild(val component: NewPatientComponent) : Child()
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object Home : Config()

        @Serializable
        data object Calendar : Config()

        @Serializable
        data object Patients : Config()

        @Serializable
        data object Report : Config()

        @Serializable
        data class NewPatient(val selectedPatientId: Long?) : Config()
    }
}