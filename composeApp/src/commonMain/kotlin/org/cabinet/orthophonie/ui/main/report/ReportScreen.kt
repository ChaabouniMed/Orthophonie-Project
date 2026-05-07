package org.cabinet.orthophonie.ui.main.report

import androidx.compose.foundation.background
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

        // 1. Filtres Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PatientSelector(state, onEvent)
                PeriodToggle(state, onEvent)
            }
        }

        // 2. Dashbord KPIs
        KPIGrid(state)

        // 3. Summary Section
        if (state.selectedPatientId != null && state.patientStats != null) {
            PatientStatsSection(state.patientStats, state.selectedPeriod)
        } else {
            GlobalStatsSection(state)
        }

        // 4. Daily Revenue Line Chart (Monthly Mode)
        if (state.selectedPeriod == ReportPeriod.MONTHLY && state.dailyRevenueCurrentMonth.isNotEmpty()) {
            ChartCard(
                title = "Flux des Recettes du Mois", 
                subtitle = "Tendance journalière des encaissements (DZD)"
            ) {
                Column {
                    Box(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                        LineChart(
                            modifier = Modifier.width(1200.dp).height(280.dp),
                            linesParameters = listOf(
                                LineParameters(
                                    label = "Recette",
                                    data = state.dailyRevenueCurrentMonth,
                                    lineColor = Color(0xFF4CAF50),
                                    lineType = LineType.DEFAULT_LINE, // Aligné strictement avec l'axe X
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
                    LegendMarker("Montant quotidien encaissé (DZD)", Color(0xFF4CAF50))
                }
            }
        }

        // 5. Financial Distribution (Pie Chart)
        val paid = if (state.selectedPatientId != null) state.patientStats?.totalPaid ?: 0.0 else state.totalPaidAmount
        val debt = if (state.selectedPatientId != null) state.patientStats?.totalRemaining ?: 0.0 else state.totalRemainingAmount
        val totalMoney = paid + debt

        if (totalMoney > 0) {
            ChartCard(title = "Situation Financière", subtitle = "Répartition payé vs dû sur la période") {
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

        // 6. Attendance & Types
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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
            }.filter { it.percentage >= 0 }

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

            val totalTypes = state.sessionTypeCounts.values.sum().toDouble()
            val typeItems = state.sessionTypeCounts.map { (type, count) ->
                val label = if (type == SessionType.BILAN) "Bilan" else "Normal"
                val color = if (type == SessionType.BILAN) Color(0xFFFF9800) else Color(0xFF9C27B0)
                LegendItemData(label, "$count", if(totalTypes>0) (count/totalTypes*100).toInt() else 0, color)
            }.filter { it.percentage >= 0 }

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

        // 7. Yearly Evolution (LineChart)
        if (state.selectedPeriod == ReportPeriod.YEARLY && state.monthlyRevenue.isNotEmpty() && state.monthlyRevenue.any { it > 0.0 }) {
            ChartCard(title = "Évolution Annuelle", subtitle = "Revenus encaissés par mois (DZD)") {
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
                    LegendMarker("Montant mensuel (DZD)", Color(0xFF2196F3))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- COMPONENTS ---

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
        OutlinedCard(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)) {
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
        PeriodButton("Mois Actuel", state.selectedPeriod == ReportPeriod.MONTHLY, { onEvent(ReportEvents.OnPeriodChanged(ReportPeriod.MONTHLY)) }, Modifier.weight(1f))
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
fun GlobalStatsSection(state: ReportUiState) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            val label = if(state.selectedPeriod == ReportPeriod.MONTHLY) "du Mois Actuel" else "Analyse Globale"
            Text("Bilan Financier $label", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("Total Encaissé", "${state.totalPaidAmount.toInt()} DZD", Color(0xFF2E7D32))
                StatItem("Dette Totale", "${state.totalRemainingAmount.toInt()} DZD", Color(0xFFC62828))
            }
        }
    }
}

@Composable
fun PatientStatsSection(stats: PatientStats, period: ReportPeriod) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            val label = if(period == ReportPeriod.MONTHLY) "du Mois" else "Analyse Annuelle"
            Text("Résumé Individuel $label", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
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
    Card(modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            if (subtitle != null) Text(text = subtitle, fontSize = 10.sp, color = Color.Gray, lineHeight = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
