package org.cabinet.orthophonie.ui.main.sessions.new_session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.ui.main.sessions.AttendanceStatus
import org.cabinet.orthophonie.ui.main.sessions.SessionType
import org.cabinet.orthophonie.ui.main.sessions.ui_components.MyDatePickerDialog
import org.cabinet.orthophonie.ui.main.sessions.ui_components.MyTimePickerDialog

@Composable
fun NewSessionScreen(
    viewModel: NewSessionViewModel
) {
    val uiState by viewModel.state.collectAsState()

    NewSessionScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun NewSessionScreenContent(
    uiState: NewSessionState,
    onEvent: (NewSessionEvents) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        MyDatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onConfirmRequest = {
                onEvent(NewSessionEvents.OnDateSelected(it))
                showDatePicker = false
            }
        )
    }

    if (showTimePicker) {
        MyTimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onConfirmRequest = {
                onEvent(NewSessionEvents.OnTimeSelected(it))
                showTimePicker = false
            }
        )
    }

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
                onClick = { onEvent(NewSessionEvents.OnBackClicked) },
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .size(40.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Nouvelle Séance",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )
        }

        if (uiState.isLoading)
            Box(
                modifier = Modifier.fillMaxSize().fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF2196F3))
            } else
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Patient Selection
                PatientSelector(
                    selectedPatient = uiState.selectedPatient,
                    patients = uiState.patients,
                    onPatientSelected = { onEvent(NewSessionEvents.OnPatientSelected(it)) }
                )

                // Date Selection
                ClickableField(
                    label = "Date*",
                    value = uiState.selectedDate?.toString() ?: "Sélectionner la date",
                    icon = Icons.Default.CalendarToday,
                    onClick = { showDatePicker = true }
                )

                // Time Selection
                ClickableField(
                    label = "Heure*",
                    value = uiState.selectedTime?.toString() ?: "Sélectionner l'heure",
                    icon = Icons.Default.Schedule,
                    onClick = { showTimePicker = true }
                )

                // Session Type
                TypeSelector(
                    selectedType = uiState.sessionType,
                    onTypeSelected = { onEvent(NewSessionEvents.OnSessionTypeChanged(it)) }
                )

                // Amount
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = { onEvent(NewSessionEvents.OnAmountChanged(it)) },
                    label = { Text("Montant (DT)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    )
                )

                // Paid Amount
                if(uiState.attendanceStatus == AttendanceStatus.PRESENT) {
                    OutlinedTextField(
                        value = uiState.paidAmount,
                        onValueChange = { onEvent(NewSessionEvents.OnPaidAmountChanged(it)) },
                        label = { Text("Montant payé (DT)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        )
                    )
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = { onEvent(NewSessionEvents.OnNotesChanged(it)) },
                        label = { Text("Notes") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        minLines = 4,
                        maxLines = 10,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Default
                        )
                    )
                }

                if (uiState.error != null) {
                    Text(uiState.error, color = Color.Red, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onEvent(NewSessionEvents.OnSaveSession) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    enabled = !uiState.isSavingLoading
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isSavingLoading) "Enregistrement..." else "Enregistrer la séance",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
    }
}

@Composable
fun PatientSelector(
    selectedPatient: PatientRecord?,
    patients: List<PatientRecord>,
    onPatientSelected: (PatientRecord) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text("Patient*", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (selectedPatient != null) "${selectedPatient.first_name} ${selectedPatient.last_name}" else "Sélectionner un patient",
                    color = if (selectedPatient != null) Color.Black else Color.Gray
                )
                Icon(Icons.Default.ExpandMore, contentDescription = null, tint = Color.Gray)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f).background(Color.White)
            ) {
                patients.forEach { patient ->
                    DropdownMenuItem(
                        text = { Text("${patient.first_name} ${patient.last_name}") },
                        onClick = {
                            onPatientSelected(patient)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ClickableField(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = value)
            }
        }
    }
}

@Composable
fun TypeSelector(
    selectedType: SessionType,
    onTypeSelected: (SessionType) -> Unit
) {
    Column {
        Text(
            "Type de séance",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TypeChip(SessionType.NORMAL.name, selectedType == SessionType.NORMAL, onClick = { onTypeSelected(SessionType.NORMAL) })
            TypeChip(SessionType.BILAN.name, selectedType == SessionType.BILAN, onClick = { onTypeSelected(SessionType.BILAN) })
        }
    }
}

@Composable
fun TypeChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) Color(0xFFE3F2FD) else Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .height(48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (isSelected) Color(0xFF2196F3) else Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
