package org.cabinet.orthophonie.data.patients

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.utils.AppDispatchers

class PatientRepository(
    private val dao: PatientDao,
    private val dispatchers: AppDispatchers
) {

    /* ---------- GET ---------- */

    fun getPatients(): Flow<List<PatientRecord>> =
        dao.getPatients()
            .asFlow()
            .mapToList(dispatchers.io)
            .distinctUntilChanged()

    suspend fun getPatientById(id: Long): PatientRecord? =
        withContext(dispatchers.io) {
            dao.getPatientById(id)
                .executeAsOneOrNull()
        }

    fun getActivePatients(): Flow<List<PatientRecord>> =
        dao.getActivePatients()
            .asFlow()
            .mapToList(dispatchers.io)
            .distinctUntilChanged()

    fun getPatientsBySchool(school: String): Flow<List<PatientRecord>> =
        dao.getPatientsBySchool(school)
            .asFlow()
            .mapToList(dispatchers.io)
            .distinctUntilChanged()

    fun getRecentPatients(limit: Long): Flow<List<PatientRecord>> =
        dao.getRecentPatients(limit)
            .asFlow()
            .mapToList(dispatchers.io)
            .distinctUntilChanged()

    /* ---------- WRITE ---------- */

    suspend fun insertPatient(patient: PatientRecord) =
        withContext(dispatchers.io) {
            dao.insertPatient(patient)
        }

    suspend fun updatePatient(patient: PatientRecord) =
        withContext(dispatchers.io) {
            dao.updatePatient(patient)
        }

    suspend fun deletePatient(id: Long) =
        withContext(dispatchers.io) {
            dao.deletePatientById(id)
        }
}
