package org.cabinet.orthophonie.ui.main.patients.new_patient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NewPatientScreen(
    viewModel: NewPatientViewModel
) {
    val uiState by viewModel.state.collectAsState()

    NewPatientScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun NewPatientScreenContent(
    uiState: NewPatientState,
    onEvent: (NewPatientEvents) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onEvent(NewPatientEvents.OnBackClicked) },
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .size(40.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Nouveau Patient",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2196F3))
                }
            } else {
                PatientTextField(
                    value = uiState.firstName,
                    onValueChange = { onEvent(NewPatientEvents.OnFirstNameChanged(it)) },
                    label = "Prénom",
                    placeholder = "Entrez le prénom"
                )

                PatientTextField(
                    value = uiState.lastName,
                    onValueChange = { onEvent(NewPatientEvents.OnLastNameChanged(it)) },
                    label = "Nom",
                    placeholder = "Entrez le nom"
                )

                PatientTextField(
                    value = uiState.dateBirth,
                    onValueChange = { onEvent(NewPatientEvents.OnDateBirthChanged(it)) },
                    label = "Date de naissance",
                    placeholder = "JJ/MM/AAAA"
                )

                PatientTextField(
                    value = uiState.contactParent,
                    onValueChange = { onEvent(NewPatientEvents.OnContactParentChanged(it)) },
                    label = "Contact Parent",
                    placeholder = "Numéro de téléphone"
                )

                PatientTextField(
                    value = uiState.school,
                    onValueChange = { onEvent(NewPatientEvents.OnSchoolChanged(it)) },
                    label = "École",
                    placeholder = "Nom de l'école"
                )

                PatientTextField(
                    value = uiState.schoolClass,
                    onValueChange = { onEvent(NewPatientEvents.OnClassChanged(it)) },
                    label = "Classe",
                    placeholder = "Ex: 3ème année"
                )

                // Status Selector
                Column {
                    Text(
                        text = "Statut",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusChip(
                            text = "Actif",
                            isSelected = uiState.status == "Active",
                            onClick = { onEvent(NewPatientEvents.OnStatusChanged("Active")) }
                        )
                        StatusChip(
                            text = "Archivé",
                            isSelected = uiState.status == "Archived",
                            onClick = { onEvent(NewPatientEvents.OnStatusChanged("Archived")) }
                        )
                    }
                }

                if (uiState.error != null) {
                    Text(
                        text = uiState.error,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onEvent(NewPatientEvents.OnSavePatient) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    enabled = !uiState.isSavingLoading
                ) {
                    if (uiState.isSavingLoading) {
                        Text("Enregistrement...", fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Enregistrer le patient",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun PatientTextField(
    value: String?,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value ?: "",
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.LightGray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFFE0E0E0)
            ),
            singleLine = true
        )
    }
}

@Composable
fun StatusChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White,
            contentColor = if (isSelected) Color(0xFF2196F3) else Color.Gray
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        modifier = Modifier.height(40.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.Medium)
    }
}