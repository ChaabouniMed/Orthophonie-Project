package org.cabinet.orthophonie.utils.time

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    // Utilisation de derivedStateOf pour éviter les recompositions inutiles
    val confirmEnabled by remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = confirmEnabled,
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Conversion correcte des millisecondes UTC en LocalDate
                        val date = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.UTC).date
                        onConfirmRequest(date)
                    }
                }
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: (LocalTime) -> Unit
){
    val state = rememberTimePickerState(is24Hour = true)

    BaseTimePickerDialog(
        onCancel = onDismissRequest,
        onConfirm = {
            onConfirmRequest(LocalTime(state.hour, state.minute))
        },
    ){
        TimePicker(state = state)
    }
}

@Composable
fun BaseTimePickerDialog(
    title: String = "Sélectionner l'heure",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge, // Coins arrondis M3
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End // Aligne les boutons à droite
                ) {
                    TextButton(onClick = onCancel) {
                        Text("Annuler")
                    }
                    TextButton(onClick = onConfirm) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

