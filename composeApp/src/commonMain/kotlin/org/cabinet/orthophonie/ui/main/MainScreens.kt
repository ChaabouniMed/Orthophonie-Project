package org.cabinet.orthophonie.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import org.cabinet.orthophonie.ui.main.home.HomeScreen

@Composable
fun MainScreens(
    component: MainComponent,
) {
    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { padding ->
        MainScreensContent(
            component = component,
            padding = padding,
        )
    }
}

@Composable
fun MainScreensContent(
    component: MainComponent,
    padding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Children(stack = component.childStack) {
                when (val child = it.instance) {
                    is MainComponent.Child.HomeChild -> HomeScreen()
                    is MainComponent.Child.CalendarChild -> CalendarScreenContent()
                    is MainComponent.Child.ReportChild -> ReportScreenContent()
                    is MainComponent.Child.PatientsChild -> PatientsScreenContent()
                }
            }
        }
    }

}

@Composable
fun CalendarScreenContent() {

}

@Composable
fun ReportScreenContent() {

}

@Composable
fun PatientsScreenContent() {

}

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            selected = true,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Groups, contentDescription = null) },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            selected = false,
            onClick = { }
        )
    }
}