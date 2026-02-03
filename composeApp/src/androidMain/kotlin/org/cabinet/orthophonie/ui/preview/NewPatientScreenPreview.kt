package org.cabinet.orthophonie.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.cabinet.orthophonie.ui.main.patients.new_patient.NewPatientScreenContent
import org.cabinet.orthophonie.ui.main.patients.new_patient.NewPatientState
import org.cabinet.orthophonie.ui.theme.AppTheme

@Preview(
    showBackground = true
)
@Composable
fun NewPatientScreenPreview() {
    AppTheme {
        NewPatientScreenContent(
            uiState = NewPatientState(
                isLoading = true
            ),
            onEvent = {}
        )
    }
}