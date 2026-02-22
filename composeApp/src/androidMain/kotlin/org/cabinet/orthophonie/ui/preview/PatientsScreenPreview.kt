package org.cabinet.orthophonie.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.ui.main.patients.PatientStatus
import org.cabinet.orthophonie.ui.main.patients.PatientsScreenContent
import org.cabinet.orthophonie.ui.main.patients.PatientsUiState
import org.cabinet.orthophonie.ui.theme.AppTheme

@Preview(
    showBackground = true
)
@Composable
fun PatientsScreenPreview() {
    AppTheme {
        PatientsScreenContent(
            uiState = PatientsUiState(
                filteredPatients = listOf(
                    PatientRecord(
                        id = 0,
                        first_name = "Toto",
                        last_name = "Tata",
                        date_birth = "2000-01-01",
                        contact_parent = "22365874",
                        school = "Lycee",
                        school_class = "5",
                        status = PatientStatus.ACTIVE,
                        creation_date = "2023-01-01"
                    ),
                    PatientRecord(
                        id = 1,
                        first_name = "Toto",
                        last_name = "Tata",
                        date_birth = "2000-01-01",
                        contact_parent = "22365874",
                        school = "Lycee",
                        school_class = "5",
                        status = PatientStatus.ACTIVE,
                        creation_date = "2023-01-01"
                    ),
                    PatientRecord(
                        id = 2,
                        first_name = "Toto",
                        last_name = "Tata",
                        date_birth = "2000-01-01",
                        contact_parent = "22365874",
                        school = "Lycee",
                        school_class = "5",
                        status = PatientStatus.ARCHIVED,
                        creation_date = "2023-01-01"
                    ),
                    PatientRecord(
                        id = 3,
                        first_name = "Toto",
                        last_name = "Tata",
                        date_birth = "2000-01-01",
                        contact_parent = "22365874",
                        school = "Lycee",
                        school_class = "5",
                        status = PatientStatus.ARCHIVED,
                        creation_date = "2023-01-01"
                    )
                )
            ),
            onEvent = {}
        )
    }
}