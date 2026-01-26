package org.cabinet.orthophonie.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.cabinet.orthophonie.ui.main.home.HomeScreen
import org.cabinet.orthophonie.ui.theme.AppTheme

@Preview(
    showBackground = true
)
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen()
    }
}