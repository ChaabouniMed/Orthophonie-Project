package org.cabinet.orthophonie.ui.main.report

import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.ui.main.sessions.AttendanceStatus
import org.cabinet.orthophonie.ui.main.sessions.SessionType

data class ReportUiState(
    val isLoading: Boolean = true,
    val patients: List<PatientRecord> = emptyList(),
    val selectedPatientId: Long? = null,
    val selectedPeriod: ReportPeriod = ReportPeriod.YEARLY,
    val selectedMonth: Int = 1, // 1-12
    val selectedYear: Int = 2024,
    
    // Global/Yearly Stats
    val monthlyRevenue: List<Double> = emptyList(),
    val revenueLabels: List<String> = emptyList(),

    val totalPaidAmount: Double = 0.0,
    val totalRemainingAmount: Double = 0.0,
    val totalRevenue: Double = 0.0,

    val sessionTypeCounts: Map<SessionType, Int> = emptyMap(),
    val attendanceCounts: Map<AttendanceStatus, Int> = emptyMap(),

    val weeklyActivity: List<Double> = emptyList(), 
    val weeklyLabels: List<String> = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"),

    val patientAcquisition: List<Double> = emptyList(),
    val acquisitionLabels: List<String> = emptyList(),
    
    // Current Period Stats (Month specific)
    val dailyRevenue: List<Double> = emptyList(),
    val dailyRevenueLabels: List<String> = emptyList(),
    
    // KPIs
    val attendanceRate: Int = 0, // Percentage
    val collectionRate: Int = 0, // Percentage of money collected
    val averageSessionAmount: Double = 0.0,
    val totalSessionsCount: Int = 0,

    // Patient specific stats
    val patientStats: PatientStats? = null,

    val error: String? = null
)

enum class ReportPeriod {
    YEARLY, MONTHLY
}

data class PatientStats(
    val presentCount: Int = 0,
    val absentCount: Int = 0,
    val paidSessionsCount: Int = 0,
    val totalPaid: Double = 0.0,
    val totalRemaining: Double = 0.0,
    val totalSessions: Int = 0,
    val attendanceRate: Int = 0
)
