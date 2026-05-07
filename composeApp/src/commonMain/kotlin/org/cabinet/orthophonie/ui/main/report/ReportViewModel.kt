package org.cabinet.orthophonie.ui.main.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.cabinet.orthophonie.data.patients.PatientRepository
import org.cabinet.orthophonie.data.sessions.SessionRepository
import org.cabinet.orthophonie.database.GetSessions
import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.ui.main.sessions.AttendanceStatus
import org.cabinet.orthophonie.utils.AppDispatchers
import kotlin.time.Clock
import kotlin.time.Instant

class ReportViewModel(
    private val patientRepository: PatientRepository,
    private val sessionRepository: SessionRepository,
    private val dispatchers: AppDispatchers
) : ViewModel() {

    private val _state = MutableStateFlow(ReportUiState())
    val state = _state.asStateFlow()

    private val _selectedPatientId = MutableStateFlow<Long?>(null)
    private val _selectedPeriod = MutableStateFlow(ReportPeriod.YEARLY)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(dispatchers.io) {
            combine(
                patientRepository.patients,
                sessionRepository.sessions,
                _selectedPatientId,
                _selectedPeriod
            ) { patients, sessions, selectedId, period ->
                calculateStats(patients, sessions, selectedId, period)
            }.collect { newState ->
                _state.update { newState }
            }
        }
    }

    private fun calculateStats(
        patients: List<PatientRecord>?,
        sessions: List<GetSessions>?,
        selectedId: Long?,
        period: ReportPeriod
    ): ReportUiState {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentYear = now.year
        val currentMonth = now.monthNumber

        // 1. Filtrage initial par patient
        var filteredSessions = if (selectedId != null) {
            sessions?.filter { it.patient_id == selectedId }
        } else {
            sessions
        }

        // 2. Filtrage par période pour les KPIs et les graphiques circulaires
        val periodSessions = when (period) {
            ReportPeriod.YEARLY -> filteredSessions?.filter {
                try {
                    val dt = Instant.parse(it.start_time).toLocalDateTime(TimeZone.currentSystemDefault())
                    dt.year == currentYear
                } catch (e: Exception) { false }
            }
            ReportPeriod.MONTHLY -> filteredSessions?.filter {
                try {
                    val dt = Instant.parse(it.start_time).toLocalDateTime(TimeZone.currentSystemDefault())
                    dt.year == currentYear && dt.monthNumber == currentMonth
                } catch (e: Exception) { false }
            }
        }

        // --- CALCULS DES KPIs (basés sur periodSessions) ---
        var periodPaid = 0.0
        var periodRemaining = 0.0
        var periodExpected = 0.0
        var periodPaidCount = 0
        
        periodSessions?.forEach {
            val amount = it.amount ?: 30.0
            val paid = it.paid_amount ?: 0.0
            if (it.attendance_status == AttendanceStatus.PRESENT) {
                periodPaid += paid
                periodRemaining += (amount - paid).coerceAtLeast(0.0)
                periodExpected += amount
                if (paid >= amount && amount > 0) periodPaidCount++
            }
        }

        val typeCounts = periodSessions?.groupingBy { it.session_type }?.eachCount() ?: emptyMap()
        val attendanceCounts = periodSessions?.groupingBy { it.attendance_status }?.eachCount() ?: emptyMap()

        val totalSessions = periodSessions?.size ?: 0
        val presentCount = attendanceCounts[AttendanceStatus.PRESENT] ?: 0
        val absentCount = attendanceCounts[AttendanceStatus.ABSENT] ?: 0
        
        val attendanceRate = if (presentCount + absentCount > 0) {
            (presentCount.toDouble() / (presentCount + absentCount) * 100).toInt()
        } else 0
        
        val collectionRate = if (periodExpected > 0) {
            (periodPaid / periodExpected * 100).toInt()
        } else 0
        
        val averageAmount = if (presentCount > 0) periodExpected / presentCount else 0.0

        // --- CALCULS DES GRAPHIQUES ---

        // 1. Monthly Revenue (Yearly Graph)
        val monthlyRevenueMap = mutableMapOf<Int, Double>()
        filteredSessions?.filter { 
            try {
                val dt = Instant.parse(it.start_time).toLocalDateTime(TimeZone.currentSystemDefault())
                dt.year == currentYear
            } catch (e: Exception) { false }
        }?.forEach {
            try {
                val dt = Instant.parse(it.start_time).toLocalDateTime(TimeZone.currentSystemDefault())
                monthlyRevenueMap[dt.month.number] = (monthlyRevenueMap[dt.month.number] ?: 0.0) + (it.paid_amount ?: 0.0)
            } catch (e: Exception) {}
        }
        val revenueData = (1..12).map { monthlyRevenueMap[it] ?: 0.0 }
        val revenueLabels = listOf("Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc")

        // 2. Daily Revenue (Monthly Graph)
        val dailyRevenueMap = mutableMapOf<Int, Double>()
        filteredSessions?.filter {
            try {
                val dt = Instant.parse(it.start_time).toLocalDateTime(TimeZone.currentSystemDefault())
                dt.year == currentYear && dt.monthNumber == currentMonth
            } catch (e: Exception) { false }
        }?.forEach {
            try {
                val dt = Instant.parse(it.start_time).toLocalDateTime(TimeZone.currentSystemDefault())
                dailyRevenueMap[dt.dayOfMonth] = (dailyRevenueMap[dt.dayOfMonth] ?: 0.0) + (it.paid_amount ?: 0.0)
            } catch (e: Exception) {}
        }
        val daysInMonth = when (currentMonth) {
            2 -> if (currentYear % 4 == 0) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
        val dailyRevenueData = (1..daysInMonth).map { dailyRevenueMap[it] ?: 0.0 }
        val dailyLabels = (1..daysInMonth).map { it.toString() }

        // 3. Weekly Activity (Current Week View)
        val weeklyActivity = DoubleArray(7)
        periodSessions?.forEach {
            try {
                val dt = Instant.parse(it.start_time).toLocalDateTime(TimeZone.currentSystemDefault())
                weeklyActivity[dt.dayOfWeek.isoDayNumber - 1] += 1.0
            } catch (e: Exception) {}
        }

        // 4. Patient Stats
        val patientStats = if (selectedId != null) {
            PatientStats(
                presentCount = presentCount,
                absentCount = absentCount,
                paidSessionsCount = periodPaidCount,
                totalPaid = periodPaid,
                totalRemaining = periodRemaining,
                totalSessions = totalSessions,
                attendanceRate = attendanceRate
            )
        } else null

        return ReportUiState(
            isLoading = false,
            patients = patients ?: listOf(),
            selectedPatientId = selectedId,
            selectedPeriod = period,
            monthlyRevenue = revenueData,
            revenueLabels = revenueLabels,
            dailyRevenueCurrentMonth = dailyRevenueData,
            dailyRevenueLabels = dailyLabels,
            totalPaidAmount = periodPaid,
            totalRemainingAmount = periodRemaining,
            totalRevenue = periodExpected,
            sessionTypeCounts = typeCounts,
            attendanceCounts = attendanceCounts,
            weeklyActivity = weeklyActivity.toList(),
            attendanceRate = attendanceRate,
            collectionRate = collectionRate,
            averageSessionAmount = averageAmount,
            totalSessionsCount = totalSessions,
            patientStats = patientStats
        )
    }

    fun onEvent(event: ReportEvents) {
        when (event) {
            is ReportEvents.OnPatientSelected -> _selectedPatientId.value = event.patientId
            is ReportEvents.OnPeriodChanged -> _selectedPeriod.value = event.period
            ReportEvents.RefreshStats -> loadData()
        }
    }
}
