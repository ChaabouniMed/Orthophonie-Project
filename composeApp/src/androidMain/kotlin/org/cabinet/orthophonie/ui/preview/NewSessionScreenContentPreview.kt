package org.cabinet.orthophonie.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.cabinet.orthophonie.ui.main.sessions.new_session.NewSessionScreenContent
import org.cabinet.orthophonie.ui.main.sessions.new_session.NewSessionState
import org.cabinet.orthophonie.ui.theme.AppTheme

@Preview(
    showBackground = true
)
@Composable
fun NewSessionScreenContentPreview() {
    AppTheme {
        NewSessionScreenContent(
            uiState = NewSessionState( ),
            onEvent = {}
        )
    }
}