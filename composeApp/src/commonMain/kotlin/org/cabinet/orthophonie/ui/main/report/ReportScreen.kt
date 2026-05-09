package org.cabinet.orthophonie.ui.main.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import org.cabinet.orthophonie.ui.main.sessions.AttendanceStatus
import org.cabinet.orthophonie.ui.main.sessions.SessionType
import org.koin.compose.koinInject
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun ReportScreen(
    viewModel: ReportViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    ReportScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportScreenContent(
    state: ReportUiState,
    onEvent: (ReportEvents) -> Unit
) {
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF2196F3))
        }
        return
    }

    val months = listOf("Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre")
    val currentPeriodLabel = if (state.selectedPeriod == ReportPeriod.MONTHLY) "${months[state.selectedMonth - 1]} ${state.selectedYear}" else "l'année ${state.selectedYear}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Statistiques & Rapports",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1C1E)
        )

        // 1. Filter Section (Patient + Period Selection)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PatientSelector(state, onEvent)
                HorizontalDivider(color = Color(0xFFF0F2F5))
                PeriodToggle(state, onEvent)
                MonthYearPickers(state, onEvent)
            }
        }

        // 2. Dashbord KPIs
        KPIGrid(state)

        // 3. COLOR LEGEND GUIDE
        ColorSemanticGuide()

        // 4. Bilan Summary
        if (state.selectedPatientId != null && state.patientStats != null) {
            PatientStatsSection(state.patientStats, currentPeriodLabel)
        } else {
            GlobalStatsSection(state, currentPeriodLabel)
        }

        // 5. Daily Revenue (Monthly Mode)
        if (state.selectedPeriod == ReportPeriod.MONTHLY && state.dailyRevenue.isNotEmpty()) {
            ChartCard(
                title = "Recettes Quotidiennes", 
                subtitle = "Tendance des encaissements de $currentPeriodLabel (DZD)"
            ) {
                Column {
                    Box(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                        LineChart(
                            modifier = Modifier.width(1000.dp).height(250.dp),
                            linesParameters = listOf(
                                LineParameters(
                                    label = "Ventes",
                                    data = state.dailyRevenue,
                                    lineColor = Color(0xFF4CAF50),
                                    lineType = LineType.DEFAULT_LINE,
                                    lineShadow = true
                                )
                            ),
                            isGrid = true,
                            gridColor = Color.LightGray.copy(alpha = 0.5f),
                            xAxisData = state.dailyRevenueLabels,
                            animateChart = true,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendMarker("Encaissement par jour", Color(0xFF4CAF50))
                }
            }
        }

        // 6. Financial Distribution (Pie Chart)
        val paid = if (state.selectedPatientId != null) state.patientStats?.totalPaid ?: 0.0 else state.totalPaidAmount
        val debt = if (state.selectedPatientId != null) state.patientStats?.totalRemaining ?: 0.0 else state.totalRemainingAmount
        val totalMoney = paid + debt

        if (totalMoney > 0) {
            ChartCard(title = "Situation Financière", subtitle = "Répartition pour $currentPeriodLabel") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PieChart(
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        pieChartData = listOfNotNull(
                            if (paid > 0) PieChartData(data = paid, color = Color(0xFF4CAF50), partName = "Payé") else null,
                            if (debt > 0) PieChartData(data = debt, color = Color(0xFFF44336), partName = "Dette") else null
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailLegend(items = listOf(
                        LegendItemData("Total Payé", "${paid.toInt()} DZD", if(totalMoney>0) (paid/totalMoney*100).toInt() else 0, Color(0xFF4CAF50)),
                        LegendItemData("Total Dette", "${debt.toInt()} DZD", if(totalMoney>0) (debt/totalMoney*100).toInt() else 0, Color(0xFFF44336))
                    ))
                }
            }
        }

        // 7. Présences et Types
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Attendance
            val totalSess = state.attendanceCounts.values.sum().toDouble()
            val attItems = state.attendanceCounts.map { (status, count) ->
                val label = when(status) {
                    AttendanceStatus.PRESENT -> "Présent"
                    AttendanceStatus.ABSENT -> "Absent"
                    AttendanceStatus.PENDING -> "Attente"
                    AttendanceStatus.CANCELED -> "Annulé"
                }
                val color = when (status) {
                    AttendanceStatus.PRESENT -> Color(0xFF4CAF50)
                    AttendanceStatus.ABSENT -> Color(0xFFF44336)
                    else -> Color(0xFF2196F3)
                }
                LegendItemData(label, "$count", if(totalSess>0) (count/totalSess*100).toInt() else 0, color)
            }.filter { it.value.toInt() > 0 }

            if (attItems.isNotEmpty()) {
                ChartCard(title = "Présences", modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PieChart(
                            modifier = Modifier.fillMaxWidth().height(130.dp),
                            pieChartData = attItems.map { PieChartData(data = it.value.toDouble(), color = it.color, partName = it.label) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailLegend(items = attItems, vertical = true)
                    }
                }
            }

            // Session Types
            val totalTypes = state.sessionTypeCounts.values.sum().toDouble()
            val typeItems = state.sessionTypeCounts.map { (type, count) ->
                val label = if (type == SessionType.BILAN) "Bilan" else "Normal"
                val color = if (type == SessionType.BILAN) Color(0xFFFF9800) else Color(0xFF9C27B0)
                LegendItemData(label, "$count", if(totalTypes>0) (count/totalTypes*100).toInt() else 0, color)
            }.filter { it.value.toInt() > 0 }

            if (typeItems.isNotEmpty()) {
                ChartCard(title = "Types", modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PieChart(
                            modifier = Modifier.fillMaxWidth().height(130.dp),
                            pieChartData = typeItems.map { PieChartData(data = it.value.toDouble(), color = it.color, partName = it.label) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailLegend(items = typeItems, vertical = true)
                    }
                }
            }
        }

        // 8. Yearly Evolution (LineChart for the selected year)
        if (state.selectedPeriod == ReportPeriod.YEARLY && state.monthlyRevenue.isNotEmpty() && state.monthlyRevenue.any { it > 0.0 }) {
            ChartCard(title = "Évolution Annuelle", subtitle = "Recettes par mois pour ${state.selectedYear}") {
                Column {
                    Box(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                        LineChart(
                            modifier = Modifier.width(800.dp).height(240.dp),
                            linesParameters = listOf(
                                LineParameters(
                                    label = "Encaissé",
                                    data = state.monthlyRevenue,
                                    lineColor = Color(0xFF2196F3),
                                    lineType = LineType.DEFAULT_LINE,
                                    lineShadow = true
                                )
                            ),
                            isGrid = true,
                            gridColor = Color.LightGray.copy(alpha = 0.5f),
                            xAxisData = state.revenueLabels,
                            animateChart = true,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendMarker("Montant cumulé mensuel", Color(0xFF2196F3))
                }
            }
        }

        // 9. Cabinet Growth Chart (Only in Global view)
        if (state.selectedPatientId == null && state.patientAcquisition.isNotEmpty() && state.patientAcquisition.any { it > 0.0 }) {
            ChartCard(title = "Croissance du Cabinet", subtitle = "Nouveaux patients inscrits en ${state.selectedYear}") {
                Column {
                    Box(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                        LineChart(
                            modifier = Modifier.width(800.dp).height(240.dp),
                            linesParameters = listOf(
                                LineParameters(
                                    label = "Inscriptions",
                                    data = state.patientAcquisition,
                                    lineColor = Color(0xFF9C27B0),
                                    lineType = LineType.DEFAULT_LINE,
                                    lineShadow = true
                                )
                            ),
                            isGrid = true,
                            gridColor = Color.LightGray.copy(alpha = 0.5f),
                            xAxisData = state.acquisitionLabels,
                            animateChart = true,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendMarker("Nombre d'inscriptions par mois", Color(0xFF9C27B0))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- UI COMPONENTS ---

@Composable
fun MonthYearPickers(state: ReportUiState, onEvent: (ReportEvents) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (state.selectedPeriod == ReportPeriod.MONTHLY) {
            MonthSelector(state, onEvent, Modifier.weight(1f))
        }
        YearSelector(state, onEvent, Modifier.weight(1f))
    }
}

@Composable
fun MonthSelector(state: ReportUiState, onEvent: (ReportEvents) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val months = listOf("Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre")
    
    Box(modifier = modifier) {
        OutlinedCard(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = months[state.selectedMonth - 1], fontSize = 13.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            months.forEachIndexed { index, name ->
                DropdownMenuItem(text = { Text(name) }, onClick = { onEvent(ReportEvents.OnMonthChanged(index + 1)); expanded = false })
            }
        }
    }
}

@Composable
fun YearSelector(state: ReportUiState, onEvent: (ReportEvents) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
    val years = (currentYear - 5..currentYear + 2).toList()
    
    Box(modifier = modifier) {
        OutlinedCard(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = state.selectedYear.toString(), fontSize = 13.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            years.forEach { year ->
                DropdownMenuItem(text = { Text(year.toString()) }, onClick = { onEvent(ReportEvents.OnYearChanged(year)); expanded = false })
            }
        }
    }
}

@Composable
fun PatientSelector(state: ReportUiState, onEvent: (ReportEvents) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selectedPatient = state.patients.find { it.id == state.selectedPatientId }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.FilterList, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Filtrer par Patient", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedCard(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = selectedPatient?.let { "${it.first_name} ${it.last_name}" } ?: "Tous les patients (Vue globale)", color = if (selectedPatient != null) Color.Black else Color.Gray, fontSize = 14.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.85f).background(Color.White)) {
            DropdownMenuItem(text = { Text("Tous les patients (Global)") }, onClick = { onEvent(ReportEvents.OnPatientSelected(null)); expanded = false })
            state.patients.forEach { patient ->
                DropdownMenuItem(text = { Text("${patient.first_name} ${patient.last_name}") }, onClick = { onEvent(ReportEvents.OnPatientSelected(patient.id)); expanded = false })
            }
        }
    }
}

@Composable
fun PeriodToggle(state: ReportUiState, onEvent: (ReportEvents) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PeriodButton("Année", state.selectedPeriod == ReportPeriod.YEARLY, { onEvent(ReportEvents.OnPeriodChanged(ReportPeriod.YEARLY)) }, Modifier.weight(1f))
        PeriodButton("Mois", state.selectedPeriod == ReportPeriod.MONTHLY, { onEvent(ReportEvents.OnPeriodChanged(ReportPeriod.MONTHLY)) }, Modifier.weight(1f))
    }
}

@Composable
fun PeriodButton(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier.height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = if (selected) Color(0xFF2196F3) else Color(0xFFF0F2F5), contentColor = if (selected) Color.White else Color.Gray), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(0.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ColorSemanticGuide() {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            ColorGuideItem("Réussite/Payé", Color(0xFF4CAF50))
            ColorGuideItem("Dette/Absent", Color(0xFFF44336))
            ColorGuideItem("Info/Attente", Color(0xFF2196F3))
        }
    }
}

@Composable
fun ColorGuideItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = Color.DarkGray)
    }
}

data class LegendItemData(val label: String, val value: String, val percentage: Int, val color: Color)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailLegend(items: List<LegendItemData>, vertical: Boolean = false) {
    if (vertical) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { items.forEach { LegendRow(it) } }
    } else {
        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEach { LegendRow(it); Spacer(modifier = Modifier.width(16.dp)) }
        }
    }
}

@Composable
fun LegendRow(item: LegendItemData) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(4.dp)).background(item.color))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = item.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            if (item.value.isNotEmpty()) Text(text = "${item.value} (${item.percentage}%)", fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun LegendMarker(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 11.sp, color = Color.DarkGray)
    }
}

@Composable
fun KPIGrid(state: ReportUiState) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        KPICard("Assiduité", "${state.attendanceRate}%", Icons.Default.CheckCircle, Color(0xFF4CAF50), Modifier.weight(1f))
        KPICard("Recouvrement", "${state.collectionRate}%", Icons.Default.AccountBalanceWallet, Color(0xFFFF9800), Modifier.weight(1f))
        KPICard("Séances", state.totalSessionsCount.toString(), Icons.Default.Event, Color(0xFF2196F3), Modifier.weight(1f))
    }
}

@Composable
fun KPICard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = color.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
            Text(title, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun GlobalStatsSection(state: ReportUiState, periodLabel: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Bilan Financier de $periodLabel", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("Total Encaissé", "${state.totalPaidAmount.toInt()} DZD", Color(0xFF2E7D32))
                StatItem("Dette Totale", "${state.totalRemainingAmount.toInt()} DZD", Color(0xFFC62828))
            }
        }
    }
}

@Composable
fun PatientStatsSection(stats: PatientStats, periodLabel: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Résumé Individuel de $periodLabel", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("Séances", stats.totalSessions.toString())
                StatItem("Présent", stats.presentCount.toString(), Color(0xFF4CAF50))
                StatItem("Assiduité", "${stats.attendanceRate}%", Color(0xFF2196F3))
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color = Color.Black) {
    Column {
        Text(label, fontSize = 11.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun ChartCard(title: String, subtitle: String? = null, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 10.sp, color = Color.Gray, lineHeight = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
