package org.cabinet.orthophonie.data.sessions

import app.cash.sqldelight.Query
import kotlinx.coroutines.withContext
import org.cabinet.orthophonie.database.SessionQueries
import org.cabinet.orthophonie.database.SessionRecord
import org.cabinet.orthophonie.database.GetSessions // Type généré par SQLDelight pour le JOIN
import org.cabinet.orthophonie.utils.AppDispatchers

class SessionDao(
    private val dispatchers: AppDispatchers,
    private val queries: SessionQueries
) {

    /* ---------- INSERT ---------- */

    suspend fun insertSession(session: SessionRecord) =
        withContext(dispatchers.io) {
            queries.insertSession(
                patient_id = session.patient_id,
                start_time = session.start_time,
                session_type = session.session_type,
                amount = session.amount,
                is_recurring = session.is_recurring
            )
        }

    /* ---------- UPDATE ---------- */

    /**
     * Updates the attendance and clinical notes for a session
     */
    suspend fun updateSessionAttendance(id: Long, attendanceStatus: String, notes: String?) =
        withContext(dispatchers.io) {
            queries.updateSessionAttendance(
                attendance_status = attendanceStatus,
                notes = notes,
                id = id
            )
        }

    /**
     * Record a payment for a specific session
     */
    suspend fun registerPayment(id: Long, paidAmount: Double, paidAt: String?) =
        withContext(dispatchers.io) {
            queries.registerPayment(
                paid_amount = paidAmount,
                paid_at = paidAt,
                id = id
            )
        }

    /* ---------- DELETE ---------- */

    suspend fun deleteSessionById(id: Long) =
        withContext(dispatchers.io) {
            queries.deleteSession(id)
        }

    /* ---------- GET ---------- */

    /**
     * Retrieve all sessions with patient names (JOIN)
     */
    fun getSessions(): Query<GetSessions> = queries.getSessions()

    fun getSessionsByPatient(patientId: Long): Query<SessionRecord> =
        queries.getSessionsByPatient(patientId)

    fun getSessionById(id: Long): Query<SessionRecord> =
        queries.getSessionById(id)

    /**
     * Retrieves the number of unpaid sessions (where attendance is confirmed)
     */
    fun getUnpaidSessionsCount(): Query<Long> =
        queries.getUnpaidSessionsCount()

    /**
     * Calculate the total debt of a specific patient
     */
    fun getDebtByPatient(patientId: Long): Query<Double> =
        queries.getDebtByPatient(patientId) { totalDebt ->
            // Si le patient n'a pas de séances, SUM renvoie NULL, on retourne donc 0.0
            totalDebt ?: 0.0
        }
}