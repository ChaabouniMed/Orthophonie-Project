package org.cabinet.orthophonie.data.sessions

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.cabinet.orthophonie.database.GetSessions
import org.cabinet.orthophonie.database.SessionRecord
import org.cabinet.orthophonie.ui.main.sessions.AttendanceStatus
import org.cabinet.orthophonie.utils.AppDispatchers
import kotlin.time.Clock
import kotlin.time.Instant

class SessionRepository(
    private val dao: SessionDao,
    private val dispatchers: AppDispatchers,
    applicationScope: CoroutineScope
) {

    private val allSessionsFlow: Flow<List<GetSessions>> = dao.getSessions()
        .asFlow()
        .mapToList(dispatchers.io)
        .map { list ->
            list.sortedBy { it.start_time }
        }
        .distinctUntilChanged()

    /* ---------- GET (Reactive Flows) ---------- */

    /**
     * Get all sessions with patient details for the main schedule.
     */
    val sessions: StateFlow<List<GetSessions>?> = allSessionsFlow
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /**
     * Get today sessions with patient details for the main schedule.
     */
    val todaySessions: StateFlow<List<GetSessions>?> = sessions
    .map { list ->
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        list?.filter { session ->
            try {
                Instant.parse(session.start_time)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date == today
            } catch (e: Exception) {
                false
            }
        }
    }
    .flowOn(dispatchers.io) // Le filtrage se fait sur le thread IO
    .stateIn(
    scope = applicationScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = null
    )

    /**
     * Get all sessions for a specific patient.
     */
    fun getSessionsByPatient(patientId: Long): Flow<List<SessionRecord>> =
        dao.getSessionsByPatient(patientId)
            .asFlow()
            .mapToList(dispatchers.io)
            .distinctUntilChanged()

    /**
     * Observe the total unpaid sessions count.
     */
    fun getUnpaidSessionsCount(): Flow<Long?> =
        dao.getUnpaidSessionsCount()
            .asFlow()
            .mapToOneOrNull(dispatchers.io)
            .distinctUntilChanged()

    /**
     * Observe the debt of a specific patient.
     */
    fun getDebtByPatient(patientId: Long): Flow<Double?> =
        dao.getDebtByPatient(patientId)
            .asFlow()
            .mapToOneOrNull(dispatchers.io)
            .distinctUntilChanged()

    /* ---------- GET (One-shot) ---------- */

    suspend fun getSessionById(id: Long): SessionRecord? =
        withContext(dispatchers.io) {
            dao.getSessionById(id)
                .executeAsOneOrNull()
        }

    /* ---------- WRITE ---------- */

    suspend fun insertSession(session: SessionRecord) =
        withContext(dispatchers.io) {
            dao.insertSession(session)
        }

    suspend fun updateSession(session: SessionRecord) =
        withContext(dispatchers.io) {
            dao.updateSession(session)
        }

    suspend fun updateSessionAttendance(id: Long, attendanceStatus: AttendanceStatus, notes: String? = null) =
        withContext(dispatchers.io) {
            dao.updateSessionAttendance(id, attendanceStatus, notes)
        }

    suspend fun registerPayment(id: Long, paidAmount: Double, paidAt: String?) =
        withContext(dispatchers.io) {
            dao.registerPayment(id, paidAmount, paidAt)
        }

    suspend fun deleteSession(id: Long) =
        withContext(dispatchers.io) {
            dao.deleteSessionById(id)
        }
}